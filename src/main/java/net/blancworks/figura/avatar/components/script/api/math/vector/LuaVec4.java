package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix3x4;
import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix4;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaVec4 extends ObjectWrapper<LuaVec4> {

    @LuaWhitelist
    public double x, y, z, w;

    private static final Queue<LuaVec4> pool = new LinkedList<>();

    private LuaVec4() {}

    public static LuaVec4 get() {
        LuaVec4 result = pool.poll();
        if (result == null) {
            result = new LuaVec4();
        }
        return result;
    }

    @LuaWhitelist
    public LuaVec4 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(LuaVec4 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    public String toString() {
        return String.format("vec4:{%f, %f, %f, %f}", x, y, z, w);
    }

    public LuaVec4 add(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        return result;
    }

    public LuaVec4 sub(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        return result;
    }

    public LuaVec4 mul(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        return result;
    }

    public LuaVec4 div(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        return result;
    }

    public LuaVec4 mod(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        return result;
    }

    public LuaVec4 idiv(LuaVec4 other) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        return result;
    }

    public LuaVec4 pow(double power) {
        LuaVec4 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        result.w = Math.pow(w, power);
        return result;
    }

    public double length() {
        return Math.sqrt(x*x+y*y+z*z+w*w);
    }

    public LuaVec4 scale(double factor) {
        LuaVec4 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        return result;
    }

    public LuaVec4 multiply(LuaMatrix4 mat) {
        LuaVec4 result = get();
        result.x = mat.v11*x+mat.v12*y+mat.v13*z+mat.v14*w;
        result.y = mat.v21*x+mat.v22*y+mat.v23*z+mat.v24*w;
        result.z = mat.v31*x+mat.v32*y+mat.v33*z+mat.v34*w;
        result.w = mat.v41*x+mat.v42*y+mat.v43*z+mat.v44*w;
        return result;
    }

    public LuaVec3 multiply(LuaMatrix3x4 mat) {
        LuaVec3 result = LuaVec3.get();
        result.x = mat.v11*x+mat.v12*y+mat.v13*z+mat.v14*w;
        result.y = mat.v21*x+mat.v22*y+mat.v23*z+mat.v24*w;
        result.z = mat.v31*x+mat.v32*y+mat.v33*z+mat.v34*w;
        return result;
    }

    //Lua interaction

    @Override
    public Object getFallback(String key) {
        return switch(key) {
            case "1", "r" -> x;
            case "2", "g" -> y;
            case "3", "b" -> z;
            case "4", "a" -> w;
            default -> null;
        };
    }

    public static LuaVec4 __add(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.add(vec2);
    }

    public static LuaVec4 __sub(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.sub(vec2);
    }

    public static LuaVec4 __mul(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.mul(vec2);
    }

    public static LuaVec4 __mul(LuaVec4 vec, double factor) {
        return vec.scale(factor);
    }

    public static LuaVec4 __mul(double factor, LuaVec4 vec) {
        return vec.scale(factor);
    }

    public static LuaVec4 __div(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.div(vec2);
    }

    public static LuaVec4 __div(LuaVec4 vec, double factor) {
        return vec.scale(1/factor);
    }

    public static LuaVec4 __unm(LuaVec4 vec) {
        return vec.scale(-1);
    }

    public static LuaVec4 __mod(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.mod(vec2);
    }

    public static LuaVec4 __idiv(LuaVec4 vec1, LuaVec4 vec2) {
        return vec1.idiv(vec2);
    }

    public static LuaVec4 __pow(LuaVec4 vec, double power) {
        return vec.pow(power);
    }

    public static int __len(LuaVec4 vec) {
        return 4;
    }

    public static double __call(LuaVec4 vec) {
        return vec.length();
    }

}
