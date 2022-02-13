package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec4;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaMatrix4 extends ObjectWrapper<LuaMatrix4> {

    //values are row-column. So v14 is the top right corner of the matrix.
    //can I make these public? I don't want to deal with all the setters and getters aaaa
    @LuaWhitelist
    public double v11, v12, v13, v14, v21, v22, v23, v24, v31, v32, v33, v34, v41, v42, v43, v44;

    private static Queue<LuaMatrix4> pool = new LinkedList<>();

    private LuaMatrix4() {}

    public static LuaMatrix4 get() {
        LuaMatrix4 result = pool.poll();
        if (result == null)
            result = new LuaMatrix4();
        else
            result.clear();
        return result;
    }

    private void clear() {
        v11 = v12 = v13 = v14 = v21 = v22 = v23 = v24 = v31 = v32 = v33 = v34 = v41 = v42 = v43 = v44 = 0;
    }

    @LuaWhitelist
    public LuaMatrix4 free() {
        pool.add(this);
        return this;
    }

    //TODO: methods for inverse, determinant, other common matrix operations

    public void copyFrom(LuaMatrix4 other) {
        v11 = other.v11;
        v12 = other.v12;
        v13 = other.v13;
        v14 = other.v14;
        v21 = other.v21;
        v22 = other.v22;
        v23 = other.v23;
        v24 = other.v24;
        v31 = other.v31;
        v32 = other.v32;
        v33 = other.v33;
        v34 = other.v34;
        v41 = other.v41;
        v42 = other.v42;
        v43 = other.v43;
        v44 = other.v44;
    }

    public LuaMatrix4 transpose() {
        LuaMatrix4 result = get();
        result.v11 = v11;
        result.v12 = v21;
        result.v13 = v31;
        result.v14 = v41;
        result.v21 = v12;
        result.v22 = v22;
        result.v23 = v32;
        result.v24 = v42;
        result.v31 = v13;
        result.v32 = v23;
        result.v33 = v33;
        result.v34 = v43;
        result.v41 = v14;
        result.v42 = v24;
        result.v43 = v34;
        result.v44 = v44;
        return result;
    }

    //Returns the product of the matrices, with "o" on the left.
    public LuaMatrix4 multiply(LuaMatrix4 o) {
        LuaMatrix4 result = get();

        result.v11 = o.v11*v11+o.v12*v21+o.v13*v31+o.v14*v41;
        result.v12 = o.v11*v12+o.v12*v22+o.v13*v32+o.v14*v42;
        result.v13 = o.v11*v13+o.v12*v23+o.v13*v33+o.v14*v43;
        result.v14 = o.v11*v14+o.v12*v24+o.v13*v34+o.v14*v44;

        result.v21 = o.v21*v11+o.v22*v21+o.v23*v31+o.v24*v41;
        result.v22 = o.v21*v12+o.v22*v22+o.v23*v32+o.v24*v42;
        result.v23 = o.v21*v13+o.v22*v23+o.v23*v33+o.v24*v43;
        result.v24 = o.v21*v14+o.v22*v24+o.v23*v34+o.v24*v44;

        result.v31 = o.v31*v11+o.v32*v21+o.v33*v31+o.v34*v41;
        result.v32 = o.v31*v12+o.v32*v22+o.v33*v32+o.v34*v42;
        result.v33 = o.v31*v13+o.v32*v23+o.v33*v33+o.v34*v43;
        result.v34 = o.v31*v14+o.v32*v24+o.v33*v34+o.v34*v44;

        result.v41 = o.v41*v11+o.v42*v21+o.v43*v31+o.v44*v41;
        result.v42 = o.v41*v12+o.v42*v22+o.v43*v32+o.v44*v42;
        result.v43 = o.v41*v13+o.v42*v23+o.v43*v33+o.v44*v43;
        result.v44 = o.v41*v14+o.v42*v24+o.v43*v34+o.v44*v44;

        return result;
    }

    //Lua interaction

    public static LuaMatrix4 __mul(LuaMatrix4 mat1, LuaMatrix4 mat2) {
        return mat2.multiply(mat1);
    }

    public static LuaVec4 __mul(LuaMatrix4 mat, LuaVec4 vec) {
        return vec.multiply(mat);
    }

}
