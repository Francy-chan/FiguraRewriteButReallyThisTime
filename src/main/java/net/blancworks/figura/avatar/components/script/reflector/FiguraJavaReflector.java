package net.blancworks.figura.avatar.components.script.reflector;

import com.google.common.collect.ImmutableMap;
import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import net.blancworks.figura.avatar.components.script.reflector.wrappers.ObjectWrapper;
import org.terasology.jnlua.LuaState;

public class FiguraJavaReflector implements JavaReflector {

    // -- Variables -- //
    private ImmutableMap<Class<?>, ObjectWrapper<?>> wrappers;

    private final JavaFunction indexFunction = this::lua_Index;


    public static final JavaFunction defaultIndexFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.INDEX);
    public static final JavaFunction defaultToStringFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.TOSTRING);


    // -- Constructors -- //
    public FiguraJavaReflector() {
        createObjectWrappers();
    }

    private void createObjectWrappers() {
        ImmutableMap.Builder<Class<?>, ObjectWrapper<?>> builder = new ImmutableMap.Builder<>();

        wrappers = builder.build();
    }

    // -- Functions -- //
    private Class<?> getObjectClass(Object object) {
        return object instanceof Class ? (Class) object : object.getClass();
    }

    @Override
    public JavaFunction getMetamethod(Metamethod metamethod) {
        switch (metamethod) {
            case INDEX:
                return indexFunction;
            case TOSTRING:
                return defaultToStringFunction;
        }

        return null;
    }


    /**
     * Called by lua when indexing a java object
     */
    public int lua_Index(LuaState luaState) {
        //Get object, its type, and its wrapper.
        Object object = luaState.toJavaObject(1, Object.class);
        Class<?> objectClass = getObjectClass(object);

        ObjectWrapper<?> wrapper;
        //If object IS an ObjectWrapper itself, just use itself.
        if (ObjectWrapper.class.isAssignableFrom(objectClass)) {
            wrapper = (ObjectWrapper) object;
        } else {
            wrapper = wrappers.get(objectClass);
        }

        //If there is no wrapper, return nothing. Never index objects that don't have wrappers, for security reasons.
        if (wrapper == null) return 0;

        //Set target object.
        wrapper.setTarget(object);

        //Get accessor key from the top of the stack
        String key = luaState.checkString(-1);

        //Run the wrapper's index function.
        return wrapper.lua_Index(luaState, key);
    }
}