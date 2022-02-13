package net.blancworks.figura.utils;

import org.terasology.jnlua.LuaState;

public class LuaUtils {

    public static void printStack(LuaState state) {
        System.out.println("--Top of Stack--");
        for (int i = state.getTop(); i > 0; i--) {
            System.out.println(getString(state, i));
        }
        System.out.println("--Bottom of Stack--");
    }

    public static String getString(LuaState state, int index) {
        state.pushValue(index);
        Object o = state.toJavaObject(-1, Object.class);
        state.pop(1);
        return o == null ? "null" : o.toString();
    }

}
