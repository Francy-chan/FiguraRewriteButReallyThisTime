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
        Object o = state.toJavaObject(index, Object.class);
        return o == null ? "null" : o.toString();
    }

}
