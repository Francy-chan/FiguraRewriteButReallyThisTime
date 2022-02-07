package net.blancworks.figura.avatar.components.script.lua.converter;

import net.blancworks.figura.avatar.components.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.components.script.lua.LuaFunction;
import net.blancworks.figura.avatar.components.script.lua.LuaTable;
import org.terasology.jnlua.*;

import java.util.Map;

/**
 * Special converter that Figura uses, which converts lua tables to LuaTable and lua functions to LuaFunction
 */
public class FiguraJavaConverter implements Converter {
    private static final Converter defaultConverter = DefaultConverter.getInstance();

    @Override
    public int getTypeDistance(LuaState luaState, int index, Class<?> formalType) {
        return defaultConverter.getTypeDistance(luaState, index, formalType);
    }

    @Override
    public <T> T convertLuaValue(LuaState luaState, int index, Class<T> targetType) {
        //Get the type of the lua value.
        LuaType t = luaState.type(index);

        switch (t) {
            case TABLE:
                //If the object we're converting from lua is a lua table,
                // and our target type is either Object, LuaTable, or Map,
                // return a LuaTable that represents the object.
                if ((targetType == Object.class || targetType == LuaTable.class || targetType == Map.class)) {
                    final LuaValueProxy luaValueProxy = luaState.getProxy(index);
                    return (T) new LuaTable<>() {
                        @Override
                        protected Object convertKey(int index) {
                            return getLuaState().toJavaObject(index, Object.class);
                        }

                        @Override
                        public LuaState getLuaState() {
                            return luaValueProxy.getLuaState();
                        }

                        @Override
                        public void pushValue() {
                            luaValueProxy.pushValue();
                        }
                    };
                }
                break;
            case FUNCTION:
                //If the object we're converting from lua is a function, but NOT a java function,
                // and our target type is Object or LuaFunction,
                // return a LuaFunction that represents the lua function
                if ((targetType == Object.class || targetType == LuaFunction.class) && !luaState.isJavaFunction(index))
                    return (T) new LuaFunction((FiguraLuaState) luaState, index);
        }

        //Return whatever the default converter would convert it to, as a fallback.
        return defaultConverter.convertLuaValue(luaState, index, targetType);
    }

    @Override
    public void convertJavaObject(LuaState luaState, Object object) {
        defaultConverter.convertJavaObject(luaState, object);
    }
}
