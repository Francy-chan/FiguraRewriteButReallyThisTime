package net.blancworks.figura.avatar.components.script.reflector.custom;

import org.terasology.jnlua.LuaState;

public class TypedCustomReflector<T> extends CustomReflector{

    @Override
    public int customIndex(LuaState state, Object instance, String accessName) {
        return customIndexType(state, (T) instance, accessName);
    }

    public int customIndexType(LuaState state, T instance, String accessName) {
        return 0;
    }
}
