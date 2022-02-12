package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaMatrix3 extends ObjectWrapper<LuaMatrix3> {

    @LuaWhitelist
    public double v11, v12, v13, v21, v22, v23, v31, v32, v33;

    private static Queue<LuaMatrix3> pool = new LinkedList<>();

    public static LuaMatrix3 get() {
        LuaMatrix3 result = pool.poll();
        if (result == null) {
            result = new LuaMatrix3();
        }
        return result;
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



}
