package net.blancworks.figura.avatar.script.lua.reflector;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import org.terasology.jnlua.LuaState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FiguraJavaReflector implements JavaReflector {

    // -- Variables -- //
    private static ImmutableMap<Class<?>, Supplier<ObjectWrapper<?>>> wrapperFactories;
    private static Map<Class<?>, ObjectWrapper<?>> wrapperCache = new HashMap<>();

    private final JavaFunction indexFunction = this::lua_Index;
    private final JavaFunction newIndexFunction = this::lua_newIndex;

    public static final JavaFunction defaultIndexFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.INDEX);
    public static final JavaFunction defaultNewIndexFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.NEWINDEX);
    public static final JavaFunction defaultToStringFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.TOSTRING);

    private final boolean isHost;



    // -- Constructors -- //
    public FiguraJavaReflector(boolean isHost) {
        this.isHost = isHost;
    }

    public static void setWrappers(ImmutableMap<Class<?>, Supplier<ObjectWrapper<?>>> newWrappers) {
        if (wrapperFactories == null) wrapperFactories = newWrappers;
    }

    // -- Functions -- //
    private Class<?> getObjectClass(Object object) {
        return object instanceof Class ? (Class) object : object.getClass();
    }

    @Override
    public JavaFunction getMetamethod(Metamethod metamethod) {
        return switch (metamethod) {
            case INDEX -> indexFunction;
            case NEWINDEX -> newIndexFunction;
            case TOSTRING -> defaultToStringFunction;
            default -> state -> callMetamethod(state, metamethod);
        };
    }

    private ObjectWrapper getObjectWrapper(Object targetObject, Class<?> targetClass) {
        if (targetClass == Object.class)
            return null;

        //If object IS an ObjectWrapper itself, just use itself.
        if (targetObject instanceof ObjectWrapper wrapper) {
            return wrapper;
        } else {
            var wrapper = wrapperCache.get(targetClass);

            //Get the wrapper from the cache first.
            if (wrapper != null) return wrapper;

            //If there's no wrapper, try to generate one using a factory.
            var factory = wrapperFactories.get(targetClass);

            //If there's no factory, either, try the base class.
            if (factory == null) return getObjectWrapper(targetObject, targetClass.getSuperclass());

            //If there is a factory, use it to produce an instance, and then cache the instance.
            wrapper = factory.get();
            wrapper.isHost = isHost;
            wrapperCache.put(targetClass, wrapper);

            //Return the instance we just cached.
            return wrapper;
        }
    }

    /**
     * Called by lua when indexing a java object
     */
    public int lua_Index(LuaState luaState) {
        //Get object, its type, and its wrapper.
        Object object = luaState.toJavaObject(1, Object.class);
        Class<?> objectClass = getObjectClass(object);

        ObjectWrapper<?> wrapper = getObjectWrapper(object, objectClass);

        //If there is no wrapper, return nothing. Never index objects that don't have wrappers, for security reasons.
        if (wrapper == null) return 0;

        //Set target object.
        wrapper.setTarget(object);

        //Get accessor key from the top of the stack
        String key = luaState.checkString(-1);

        //Run the wrapper's index function.
        return wrapper.lua_Index(luaState, key);
    }

    public int lua_newIndex(LuaState luaState){
        //Get object, its type, and its wrapper.
        Object object = luaState.toJavaObject(1, Object.class);
        Class<?> objectClass = getObjectClass(object);

        ObjectWrapper<?> wrapper = getObjectWrapper(object, objectClass);

        //If there is no wrapper, return nothing. Never index objects that don't have wrappers, for security reasons.
        if (wrapper == null) return 0;

        //Set target object.
        wrapper.setTarget(object);

        //Get accessor key from the top of the stack
        String key = luaState.checkString(-2);
        Object obj = luaState.toJavaObject(-1, Object.class);

        //Run the wrapper's index function.
        return wrapper.lua_newIndex(luaState, key, obj);
    }

    public int callMetamethod(LuaState luaState, Metamethod metamethod) {
        //Get object, its type, and its wrapper.
        Object object = luaState.toJavaObject(1, Object.class);
        Class<?> objectClass = getObjectClass(object);

        ObjectWrapper<?> wrapper = getObjectWrapper(object, objectClass);

        //If there is no wrapper, try second argument.
        if (wrapper == null) {
            //Get object, its type, and its wrapper.
            object = luaState.toJavaObject(2, Object.class);
            objectClass = getObjectClass(object);

            wrapper = getObjectWrapper(object, objectClass);
        }

        //If neither argument had a wrapper, return nothing. Never index objects that don't have wrappers, for security reasons.
        if (wrapper == null)
            return 0;

        //Set target object.
        wrapper.setTarget(object);

        //Attempt to run the wrapper's metamethod.
        return wrapper.callMetamethod(luaState, metamethod);
    }
}