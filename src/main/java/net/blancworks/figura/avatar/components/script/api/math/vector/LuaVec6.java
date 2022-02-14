package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaVec6 extends ObjectWrapper<LuaVec6> {

    @LuaWhitelist
    public double x, y, z, w, t, h;

    private static final Queue<LuaVec6> pool = new LinkedList<>();

    private LuaVec6() {}

    public static LuaVec6 get() {
        LuaVec6 result = pool.poll();
        if (result == null)
            result = new LuaVec6();
        else
            result.clear();
        return result;
    }

    public static LuaVec6 get(double x, double y, double z, double w, double t, double h) {
        LuaVec6 result = get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        result.h = h;
        return result;
    }

    @LuaWhitelist
    public LuaVec6 free() {
        pool.add(this);
        return this;
    }

    private void clear() {
        x = y = z = w = t = h = 0;
    }

    public void copyFrom(LuaVec6 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
        t = other.t;
        h = other.h;
    }

    public String toString() {
        return String.format("vec6:{%f, %f, %f, %f, %f, %f}", x, y, z, w, t, h);
    }

    public LuaVec6 add(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        result.t += other.t;
        result.h += other.h;
        return result;
    }

    public LuaVec6 sub(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        result.t -= other.t;
        result.h -= other.h;
        return result;
    }

    public LuaVec6 mul(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        result.t *= other.t;
        result.h *= other.h;
        return result;
    }

    public LuaVec6 div(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        result.t /= other.t;
        result.h /= other.h;
        return result;
    }

    public LuaVec6 mod(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        result.t %= other.t;
        result.h %= other.h;
        return result;
    }

    public LuaVec6 idiv(LuaVec6 other) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        result.t = Math.floor(result.t / other.t);
        result.h = Math.floor(result.h / other.h);
        return result;
    }

    public LuaVec6 pow(double power) {
        LuaVec6 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        result.w = Math.pow(w, power);
        result.t = Math.pow(t, power);
        result.h = Math.pow(h, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y+z*z+w*w+t*t+h*h);
    }

    public LuaVec6 scaled(double factor) {
        LuaVec6 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        result.t *= factor;
        result.h *= factor;
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
            case "6" -> h;
            default -> null;
        };
    }

    public static LuaVec6 __add(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.add(vec2);
    }

    public static LuaVec6 __sub(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.sub(vec2);
    }

    public static LuaVec6 __mul(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.mul(vec2);
    }

    public static LuaVec6 __mul(LuaVec6 vec, double factor) {
        return vec.scaled(factor);
    }

    public static LuaVec6 __mul(double factor, LuaVec6 vec) {
        return vec.scaled(factor);
    }

    public static LuaVec6 __div(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.div(vec2);
    }

    public static LuaVec6 __div(LuaVec6 vec, double factor) {
        return vec.scaled(1/factor);
    }

    public static LuaVec6 __unm(LuaVec6 vec) {
        return vec.scaled(-1);
    }

    public static LuaVec6 __mod(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.mod(vec2);
    }

    public static LuaVec6 __idiv(LuaVec6 vec1, LuaVec6 vec2) {
        return vec1.idiv(vec2);
    }

    public static LuaVec6 __pow(LuaVec6 vec, double power) {
        return vec.pow(power);
    }

    public static int __len(LuaVec6 vec) {
        return 6;
    }

    public static double __call(LuaVec6 vec) {
        return vec.getLength();
    }

}
