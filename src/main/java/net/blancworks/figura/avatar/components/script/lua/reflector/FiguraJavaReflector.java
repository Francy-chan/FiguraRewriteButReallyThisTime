package net.blancworks.figura.avatar.components.script.lua.reflector;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.components.model.FiguraCuboidModelPart;
import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.BlockStateWrapper;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ItemStackWrapper;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.Vec3fWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import org.terasology.jnlua.DefaultJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.JavaReflector;
import org.terasology.jnlua.LuaState;

public class FiguraJavaReflector implements JavaReflector {

    // -- Variables -- //
    private ImmutableMap<Class<?>, ObjectWrapper<?>> wrappers;

    private final JavaFunction indexFunction = this::lua_Index;
    private final JavaFunction addFunction = ls -> lua_getMathOp(ls, "__add");
    private final JavaFunction subtractFunction = ls -> lua_getMathOp(ls, "__sub");
    private final JavaFunction multiplyFunction = ls -> lua_getMathOp(ls, "__mul");
    private final JavaFunction divideFunction = ls -> lua_getMathOp(ls, "__div");
    private final JavaFunction moduloFunction = ls -> lua_getMathOp(ls, "__mod");
    private final JavaFunction powerFunction = ls -> lua_getMathOp(ls, "__pow");
    private final JavaFunction concatFunction = ls -> lua_getMathOp(ls, "__concat");


    public static final JavaFunction defaultIndexFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.INDEX);
    public static final JavaFunction defaultToStringFunction = DefaultJavaReflector.getInstance().getMetamethod(Metamethod.TOSTRING);


    // -- Constructors -- //
    public FiguraJavaReflector() {
        createObjectWrappers();
    }

    private void createObjectWrappers() {
        ImmutableMap.Builder<Class<?>, ObjectWrapper<?>> builder = new ImmutableMap.Builder<>();
        builder.put(Vec3f.class, new Vec3fWrapper());
        builder.put(FiguraCuboidModelPart.class, new ModelPartAPI());
        builder.put(FiguraModelPart.class, new ModelPartAPI());
        builder.put(ItemStack.class, new ItemStackWrapper());
        builder.put(BlockState.class, new BlockStateWrapper());

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
            case ADD:
                return addFunction;
            case SUB:
                return subtractFunction;
            case MUL:
                return multiplyFunction;
            case DIV:
                return divideFunction;
            case MOD:
                return moduloFunction;
            case POW:
                return powerFunction;
            case CONCAT:
                return concatFunction;
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

    public int lua_getMathOp(LuaState luaState, String opName) {
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

        //Run the wrapper's index function.
        return wrapper.lua_getMathOp(luaState, opName);
    }
}