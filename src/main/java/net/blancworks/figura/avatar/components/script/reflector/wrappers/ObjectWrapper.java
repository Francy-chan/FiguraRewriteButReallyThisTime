package net.blancworks.figura.avatar.components.script.reflector.wrappers;

import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
import net.blancworks.figura.avatar.components.script.reflector.LuaWhitelist;
import org.terasology.jnlua.LuaState;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;

/**
 * "Wraps" access to a specific type of object.
 */
public class ObjectWrapper<T> {
    // -- Variables -- //
    protected T target;

    //List of all whitelists, cached for re-use just in case.
    private static final HashMap<Class<?>, HashSet<String>> whitelistCache = new HashMap<>();
    protected final HashSet<String> indexWhitelist;

    // -- Constructors -- //
    public ObjectWrapper() {
        indexWhitelist = whitelistCache.computeIfAbsent(getClass(), ObjectWrapper::buildWhitelist);
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

        //Replace target object on lua stack with this object
        state.pushJavaObject(this);
        state.replace(1);

        //Run default index function on this object
        int ret = FiguraJavaReflector.defaultIndexFunction.invoke(state);

        //Replace this object with target object again
        state.pushJavaObject(target);
        state.replace(1);

        return ret;
    }

    /**
     * Called when an index is not whitelisted (or doesn't exist)
     */
    public Object getFallback(String key) {
        return null;
    }
}
