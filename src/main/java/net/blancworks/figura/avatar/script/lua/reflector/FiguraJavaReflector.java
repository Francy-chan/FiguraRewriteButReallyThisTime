package net.blancworks.figura.avatar.script.lua.reflector;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.script.api.wrappers.block.BlockStateWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.item.ItemStackWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.BiomeWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.WorldWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.LivingEntityWrapper;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
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
        //builder.put(Vec3f.class, new Vec3fWrapper());
        builder.put(FiguraModelPart.class, new ModelPartAPI());
        builder.put(ItemStack.class, new ItemStackWrapper());
        builder.put(BlockState.class, new BlockStateWrapper());
        builder.put(World.class, new WorldWrapper());
        builder.put(LivingEntity.class, new LivingEntityWrapper<>());
        builder.put(Biome.class, new BiomeWrapper());

        wrappers = builder.build();
    }

    // -- Functions -- //
    private Class<?> getObjectClass(Object object) {
        return object instanceof Class ? (Class) object : object.getClass();
    }

    @Override
    public JavaFunction getMetamethod(Metamethod metamethod) {
        return switch (metamethod) {
            case INDEX -> indexFunction;
            case TOSTRING -> defaultToStringFunction;
            default -> state -> callMetamethod(state, metamethod);
        };
    }

    private ObjectWrapper getObjectWrapper(Object targetObject, Class<?> targetClass){
        if(targetClass == Object.class)
            return null;

        //If object IS an ObjectWrapper itself, just use itself.
        if (targetObject instanceof ObjectWrapper wrapper) {
             return wrapper;
        } else {
            var wrapper = wrappers.get(targetClass);

            if(wrapper != null) {
                return wrapper;
            } else {
                return getObjectWrapper(targetObject, targetClass.getSuperclass());
            }
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