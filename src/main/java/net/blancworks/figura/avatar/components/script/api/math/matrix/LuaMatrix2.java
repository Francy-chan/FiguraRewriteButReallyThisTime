package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec2;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaMatrix2 extends ObjectWrapper<LuaMatrix2> {

    @LuaWhitelist
    public double v11 = 1, v12, v21, v22 = 1;

    private static Queue<LuaMatrix2> pool = new LinkedList<>();

    private LuaMatrix2() {}

    private LuaMatrix2(double v11, double v21,
                       double v12, double v22) {
        this.v11 = v11;
        this.v12 = v12;
        this.v21 = v21;
        this.v22 = v22;
    }

    public static LuaMatrix2 get() {
        LuaMatrix2 result = pool.poll();
        if (result == null)
            result = new LuaMatrix2();
        else
            result.resetToIdentity();
        return result;
    }

    @LuaWhitelist
    public void resetToIdentity() {
        v11 = v22 = 1;
        v12 = v21 = 0;
    }

    @LuaWhitelist
    public LuaMatrix2 free() {
        pool.add(this);
        return this;
    }

    public static LuaMatrix2 createScaleMatrix(double x, double y) {
        LuaMatrix2 result = get();
        result.v11 = x;
        result.v22 = y;
        return result;
    }

    public static LuaMatrix2 createRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);
        LuaMatrix2 result = get();
        result.v11 = result.v22 = c;
        result.v21 = s;
        result.v12 = -s;
        return result;
    }

    @LuaWhitelist
    public void copyFrom(LuaMatrix2 other) {
        v11 = other.v11;
        v12 = other.v12;
        v21 = other.v21;
        v22 = other.v22;
    }

    @LuaWhitelist
    public LuaMatrix2 transpose() {
        LuaMatrix2 result = get();
        result.v11 = v11;
        result.v12 = v21;
        result.v21 = v12;
        result.v22 = v22;
        return result;
    }

    //Returns the product of the matrices, with "o" on the left.
    public LuaMatrix2 times(LuaMatrix2 o) {
        LuaMatrix2 result = get();

        result.v11 = o.v11*v11+o.v12*v21;
        result.v12 = o.v11*v12+o.v12*v22;

        result.v21 = o.v21*v11+o.v22*v21;
        result.v22 = o.v21*v12+o.v22*v22;

        return result;
    }

    //Lua interaction

    public static LuaMatrix2 __mul(LuaMatrix2 mat1, LuaMatrix2 mat2) {
        return mat2.times(mat1);
    }

    public static LuaVec2 __mul(LuaMatrix2 mat, LuaVec2 vec) {
        return vec.multiply(mat);
    }

}
