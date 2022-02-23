package net.blancworks.figura.avatar.components.script.lua.reflector.wrappers;

import net.blancworks.figura.avatar.components.script.lua.reflector.FiguraJavaReflector;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import org.terasology.jnlua.LuaRuntimeException;
import org.terasology.jnlua.LuaState;

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

    //List of all whitelists, cached for re-use just in case.
    private static final HashMap<Class<?>, HashSet<String>> whitelistCache = new HashMap<>();
    protected final HashSet<String> indexWhitelist;

    private static final HashMap<Class<?>, HashSet<String>> metamethodCache = new HashMap<>();
    protected final HashSet<String> definedMetamethods;

    private static final HashMap<JavaFunction, JavaFunction> functionWrappers = new HashMap<>();


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
        state.pushJavaObject(this);
        state.replace(1);

        //Run default index function on this object
        int ret = FiguraJavaReflector.defaultIndexFunction.invoke(state);

        //Replace this object with target object again
        state.pushJavaObject(target);
        state.replace(1);

        JavaFunction jFunc = state.toJavaFunction(-1);
        //If top is java function
        if(jFunc != null){
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

    public int callMetamethod(LuaState state, String key) {
        if (!definedMetamethods.contains(key))
            throw new LuaRuntimeException("Attempted " + key + " on invalid type " + getClass().getName());

        //Comments are the stack
        //Can check with LuaUtils.printStack(state);

        //LuaUtils.printStack(state);
        // metamethod name
        // last arg
        // args
        // 1st arg

        //Because lua is jank, __len and __unm pass the same argument twice.
        //So we remove one of them, to keep our reflected methods clean.
        if (key.equals("__unm") || key.equals("__len"))
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
        state.call(state.getTop()-1, LuaState.MULTRET);

        //LuaUtils.printStack(state);
        // last call result
        // results
        // first call result

        //Return the size of the entire stack, since the results are the only thing on the stack
        return state.getTop();
    }

    /**
     * Called when an index is not whitelisted (or doesn't exist)
     */
    public Object getFallback(String key) {
        return null;
    }
}
