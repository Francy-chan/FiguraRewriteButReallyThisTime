package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaVec5 extends ObjectWrapper<LuaVec5> {

    @LuaWhitelist
    public double x, y, z, w, t;

    private static final Queue<LuaVec5> pool = new LinkedList<>();

    private LuaVec5() {}

    public static LuaVec5 get() {
        LuaVec5 result = pool.poll();
        if (result == null) {
            result = new LuaVec5();
        }
        return result;
    }

    @LuaWhitelist
    public LuaVec5 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(LuaVec5 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
        t = other.t;
    }

    public String toString() {
        return String.format("vec5:{%f, %f, %f, %f, %f}", x, y, z, w, t);
    }

    public LuaVec5 add(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        result.t += other.t;
        return result;
    }

    public LuaVec5 sub(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        result.t -= other.t;
        return result;
    }

    public LuaVec5 mul(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        result.t *= other.t;
        return result;
    }

    public LuaVec5 div(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        result.t /= other.t;
        return result;
    }

    public LuaVec5 mod(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        result.t %= other.t;
        return result;
    }

    public LuaVec5 idiv(LuaVec5 other) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        result.t = Math.floor(result.t / other.t);
        return result;
    }

    public LuaVec5 pow(double power) {
        LuaVec5 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        result.w = Math.pow(w, power);
        result.t = Math.pow(t, power);
        return result;
    }

    public double length() {
        return Math.sqrt(x*x+y*y+z*z+w*w+t*t);
    }

    public LuaVec5 scale(double factor) {
        LuaVec5 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        result.t *= factor;
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
            case "5" -> t;
            default -> null;
        };
    }

    public static LuaVec5 __add(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.add(vec2);
    }

    public static LuaVec5 __sub(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.sub(vec2);
    }

    public static LuaVec5 __mul(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.mul(vec2);
    }

    public static LuaVec5 __mul(LuaVec5 vec, double factor) {
        return vec.scale(factor);
    }

    public static LuaVec5 __mul(double factor, LuaVec5 vec) {
        return vec.scale(factor);
    }

    public static LuaVec5 __div(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.div(vec2);
    }

    public static LuaVec5 __div(LuaVec5 vec, double factor) {
        return vec.scale(1/factor);
    }

    public static LuaVec5 __unm(LuaVec5 vec) {
        return vec.scale(-1);
    }

    public static LuaVec5 __mod(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.mod(vec2);
    }

    public static LuaVec5 __idiv(LuaVec5 vec1, LuaVec5 vec2) {
        return vec1.idiv(vec2);
    }

    public static LuaVec5 __pow(LuaVec5 vec, double power) {
        return vec.pow(power);
    }

    public static int __len(LuaVec5 vec) {
        return 5;
    }

    public static double __call(LuaVec5 vec) {
        return vec.length();
    }

}
