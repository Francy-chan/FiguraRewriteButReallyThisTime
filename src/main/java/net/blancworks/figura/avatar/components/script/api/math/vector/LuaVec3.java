package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaVec3 extends ObjectWrapper<LuaVec3> {

    @LuaWhitelist
    public double x, y, z;

    private static final Queue<LuaVec3> pool = new LinkedList<>();

    private LuaVec3() {}

    public static LuaVec3 get() {
        LuaVec3 result = pool.poll();
        if (result == null) {
            result = new LuaVec3();
        }
        return result;
    }

    @LuaWhitelist
    public LuaVec3 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(LuaVec3 other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public String toString() {
        return String.format("vec3:{%f, %f, %f}", x, y, z);
    }

    public LuaVec3 add(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        return result;
    }

    public LuaVec3 sub(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        return result;
    }

    public LuaVec3 mul(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        return result;
    }

    public LuaVec3 div(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        return result;
    }

    public LuaVec3 mod(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        return result;
    }

    public LuaVec3 idiv(LuaVec3 other) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        return result;
    }

    public LuaVec3 pow(double power) {
        LuaVec3 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        return result;
    }

    public double length() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public LuaVec3 scale(double factor) {
        LuaVec3 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        return result;
    }

    @LuaWhitelist
    public LuaVec4 augment() {
        LuaVec4 result = LuaVec4.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = 1;
        return result;
    }

    //Lua interactions

    public static LuaVec3 __add(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.add(vec2);
    }

    public static LuaVec3 __sub(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.sub(vec2);
    }

    public static LuaVec3 __mul(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.mul(vec2);
    }

    public static LuaVec3 __mul(LuaVec3 vec, double factor) {
        return vec.scale(factor);
    }

    public static LuaVec3 __mul(double factor, LuaVec3 vec) {
        return vec.scale(factor);
    }

    public static LuaVec3 __div(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.div(vec2);
    }

    public static LuaVec3 __div(LuaVec3 vec, double factor) {
        return vec.scale(1/factor);
    }

    public static LuaVec3 __unm(LuaVec3 vec) {
        return vec.scale(-1);
    }

    public static LuaVec3 __mod(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.mod(vec2);
    }

    public static LuaVec3 __idiv(LuaVec3 vec1, LuaVec3 vec2) {
        return vec1.idiv(vec2);
    }

    public static LuaVec3 __pow(LuaVec3 vec, double power) {
        return vec.pow(power);
    }

    public static int __len(LuaVec3 vec) {
        return 4;
    }

    public static double __call(LuaVec3 vec) {
        return vec.length();
    }

}
