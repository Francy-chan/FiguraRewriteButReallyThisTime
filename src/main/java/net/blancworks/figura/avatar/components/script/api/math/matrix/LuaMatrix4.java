package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaMatrix4 extends ObjectWrapper<LuaMatrix4> {

    //values are row-column. So a14 is the top right corner of the matrix.
    //can I make these public? I don't want to deal with all the setters and getters aaaa
    @LuaWhitelist
    public double a11, a12, a13, a14, a21, a22, a23, a24, a31, a32, a33, a34, a41, a42, a43, a44;

    private static Queue<LuaMatrix4> pool = new LinkedList<>();

    public static LuaMatrix4 get() {
        LuaMatrix4 result = pool.poll();
        if (result == null) {
            result = new LuaMatrix4();
        }
        return result;
    }

    public void free() {
        pool.add(this);
    }

    public void copyFrom(LuaMatrix4 other) {
        a11 = other.a11;
        a12 = other.a12;
        a13 = other.a13;
        a14 = other.a14;
        a21 = other.a21;
        a22 = other.a22;
        a23 = other.a23;
        a24 = other.a24;
        a31 = other.a31;
        a32 = other.a32;
        a33 = other.a33;
        a34 = other.a34;
        a41 = other.a41;
        a42 = other.a42;
        a43 = other.a43;
        a44 = other.a44;
    }

    public LuaMatrix4 transpose() {
        LuaMatrix4 result = get();
        result.a11 = a11;
        result.a12 = a21;
        result.a13 = a31;
        result.a14 = a41;
        result.a21 = a12;
        result.a22 = a22;
        result.a23 = a32;
        result.a24 = a42;
        result.a31 = a13;
        result.a32 = a23;
        result.a33 = a33;
        result.a34 = a43;
        result.a41 = a14;
        result.a42 = a24;
        result.a43 = a34;
        result.a44 = a44;
        return result;
    }



}
