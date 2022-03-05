package net.blancworks.figura.avatar.script.lua.reflector.wrappers;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.script.lua.reflector.FiguraJavaReflector;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.utils.LuaUtils;
import org.terasology.jnlua.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * "Wraps" access to a specific type of object.
 */
public abstract class ObjectWrapper<T> {
    // -- Variables -- //
    protected T target;

    //True if the script this wrapper is attached to is owned by the client's player, not some other visible player.
    public boolean isHost;
    public T overwrite;

    //List of all whitelists, cached for re-use just in case.
    private static final HashMap<Class<?>, HashSet<String>> whitelistCache = new HashMap<>();
    protected final HashSet<String> indexWhitelist;

    private static final HashMap<Class<?>, HashSet<String>> metamethodCache = new HashMap<>();
    protected final HashSet<String> definedMetamethods;

    private final HashMap<JavaFunction, JavaFunction> functionWrappers = new HashMap<>();


    // -- Constructors -- //
    public ObjectWrapper() {
        indexWhitelist = whitelistCache.computeIfAbsent(getClass(), ObjectWrapper::buildWhitelist);

        synchronized (metamethodCache) {
            definedMetamethods = metamethodCache.computeIfAbsent(getClass(), ObjectWrapper::buildMetamethods);
        }
    }

    /**
     * Builds a whitelist for a given class using annotations.
     */
    private static HashSet<String> buildWhitelist(Class<?> c) {
        HashSet<String> whitelist = new HashSet<>();

        for (Method method : c.getMethods())
            if (method.isAnnotationPresent(LuaWhitelist.class)) whitelist.add(method.getName());
        for (Field field : c.getFields())
            if (field.isAnnotationPresent(LuaWhitelist.class)) whitelist.add(field.getName());

        return whitelist;
    }

    private static HashSet<String> buildMetamethods(Class<?> c) {
        HashSet<String> result = new HashSet<>();
        JavaReflector.Metamethod[] allMetamethods = JavaReflector.Metamethod.values();
        for (Method javaMethod : c.getMethods())
            for (JavaReflector.Metamethod metamethod : allMetamethods)
                if (javaMethod.getName().equals(metamethod.getMetamethodName()))
                    result.add(javaMethod.getName());
        return result;
    }

    // -- Functions -- //
    public void setTarget(Object obj) {
        if (overwrite != null)
            target = overwrite;
        else
            target = (T) obj;
    }


    /**
     * Called by lua when indexing this wrapper.
     */
    public int lua_Index(LuaState state, String key) {
        //If whitelist doesn't contain value, return fallback.
        if (!indexWhitelist.contains(key)) {
            state.pushJavaObject(getFallback(key));
            return 1;
        }
        //If whitelist contains value

        //put this object on the stack
        state.pushJavaObject(this);
        state.replace(1);

        //Run default index function on this object
        int ret = FiguraJavaReflector.defaultIndexFunction.invoke(state);

        JavaFunction jFunc = state.toJavaFunction(-1);
        //If top is java function
        if (jFunc != null) {
            //Get a wrapper function that replaces self with this wrapper, instead of whatever called the function
            JavaFunction actualValue = functionWrappers.computeIfAbsent(jFunc, (f) -> (s) -> {
                Object obj = s.toJavaObjectRaw(1);
                setTarget(obj);

                //Replace self (first variable) with this wrapper instead
                s.pushJavaObject(this);
                s.replace(1);

                //Actually call function
                return jFunc.invoke(s);
            });

            //Replace old java function with new one
            state.pushJavaFunction(actualValue);
            state.replace(-2);
        }

        return ret;
    }

    public int lua_newIndex(LuaState state, String key, Object value) {
        //If value isn't whitelisted, use fallback. If fallback still fails, use default.
        if (!indexWhitelist.contains(key)) {
            setFallback(key, value);
            return 0;
        }

        //Leave target object alone, call JNLua's default __newindex for it.
        int ret = FiguraJavaReflector.defaultNewIndexFunction.invoke(state);

        return ret;
    }

    public int callMetamethod(LuaState state, JavaReflector.Metamethod metamethod) {
        String mmName = metamethod.getMetamethodName();

        //Comments are the stack
        //Can check with LuaUtils.printStack(state);
        if (definedMetamethods.contains(mmName)) {
            //If there is a custom metamethod is defined, use it

            //LuaUtils.printStack(state);
            // metamethod name
            // last arg
            // args
            // 1st arg

            //Because lua is jank, __len and __unm pass the same argument twice.
            //So we remove one of them, to keep our reflected methods clean.
            if (mmName.equals("__unm") || mmName.equals("__len"))
                state.remove(1);

            //Put this object on the bottom of the stack.
            state.pushJavaObject(this);
            state.insert(1);

            //LuaUtils.printStack(state);
            // metamethod name
            // the args
            // this Java object

            //Use the metamethod name and the object on the bottom of the stack to find the metamethod JavaFunction.
            //Put that JavaFunction on the bottom of the stack.
            FiguraJavaReflector.defaultIndexFunction.invoke(state);
            state.insert(1);

            //LuaUtils.printStack(state);
            // metamethod name
            // the args
            // this Java object
            // metamethod JavaFunction

            //Remove the metamethod name, along with the copy of this object
            //leaving only the function and the args
            state.pop(1);
            state.remove(2);

            //LuaUtils.printStack(state);
            // the args
            // metamethod JavaFunction

            //Push this.class, because static purposes or something like that, jnlua
            state.pushJavaObject(this.getClass());
            state.insert(2);

            //LuaUtils.printStack(state);
            // the args
            // the Class for this java object
            // metamethod JavaFunction

            //Call the function with all our args
            state.call(state.getTop() - 1, LuaState.MULTRET);

            //LuaUtils.printStack(state);
            // last call result
            // results
            // first call result

            //Return the size of the entire stack, since the results are the only thing on the stack
            return state.getTop();
        } else {
            var mm = DefaultJavaReflector.getInstance().getMetamethod(metamethod);

            if (mm != null)
                return mm.invoke(state);
            else return 0;
        }
    }

    /**
     * Called when an index is not whitelisted (or doesn't exist)
     */
    public Object getFallback(String key) {
        return null;
    }

    /**
     * Called when a value is assigned to a new index.
     */
    public void setFallback(String key, Object value) {
    }

    @Override
    public String toString() {
        if (overwrite != null) target = overwrite;

        if (target == this) return super.toString();

        return target.toString();
    }
}
