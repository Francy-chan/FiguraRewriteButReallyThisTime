package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec3;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaMatrix3 extends ObjectWrapper<LuaMatrix3> {

    @LuaWhitelist
    public double v11, v12, v13, v21, v22, v23, v31, v32, v33;

    private static Queue<LuaMatrix3> pool = new LinkedList<>();

    private LuaMatrix3() {}

    private LuaMatrix3(double v11, double v21, double v31,
                       double v12, double v22, double v32,
                       double v13, double v23, double v33) {
        this.v11 = v11;
        this.v12 = v12;
        this.v13 = v13;
        this.v21 = v21;
        this.v22 = v22;
        this.v23 = v23;
        this.v31 = v31;
        this.v32 = v32;
        this.v33 = v33;
    }

    public static LuaMatrix3 get() {
        LuaMatrix3 result = pool.poll();
        if (result == null)
            result = new LuaMatrix3();
        else
            result.clear();
        return result;
    }

    private void clear() {
        v11 = v12 = v13 = v21 = v22 = v23 = v31 = v32 = v33 = 0;
    }

    @LuaWhitelist
    public LuaMatrix3 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(LuaMatrix3 other) {
        v11 = other.v11;
        v12 = other.v12;
        v13 = other.v13;
        v21 = other.v21;
        v22 = other.v22;
        v23 = other.v23;
        v31 = other.v31;
        v32 = other.v32;
        v33 = other.v33;
    }

    public LuaMatrix3 transpose() {
        LuaMatrix3 result = get();
        result.v11 = v11;
        result.v12 = v21;
        result.v13 = v31;
        result.v21 = v12;
        result.v22 = v22;
        result.v23 = v32;
        result.v31 = v13;
        result.v32 = v23;
        result.v33 = v33;
        return result;
    }

    //Returns the product of the matrices, with "o" on the left.
    public LuaMatrix3 multiply(LuaMatrix3 o) {
        LuaMatrix3 result = get();

        result.v11 = o.v11*v11+o.v12*v21+o.v13*v31;
        result.v12 = o.v11*v12+o.v12*v22+o.v13*v32;
        result.v13 = o.v11*v13+o.v12*v23+o.v13*v33;

        result.v21 = o.v21*v11+o.v22*v21+o.v23*v31;
        result.v22 = o.v21*v12+o.v22*v22+o.v23*v32;
        result.v23 = o.v21*v13+o.v22*v23+o.v23*v33;

        result.v31 = o.v31*v11+o.v32*v21+o.v33*v31;
        result.v32 = o.v31*v12+o.v32*v22+o.v33*v32;
        result.v33 = o.v31*v13+o.v32*v23+o.v33*v33;

        return result;
    }

    //Lua interaction

    public static LuaMatrix3 __mul(LuaMatrix3 mat1, LuaMatrix3 mat2) {
        return mat2.multiply(mat1);
    }

    public static LuaVec3 __mul(LuaMatrix3 mat, LuaVec3 vec) {
        return vec.multiply(mat);
    }

}
