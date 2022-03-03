package net.blancworks.figura.utils.math.vector;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class FiguraVec6 extends ObjectWrapper<FiguraVec6> {

    @LuaWhitelist
    public double x, y, z, w, t, h;

    private static final Queue<FiguraVec6> pool = new LinkedList<>();

    private FiguraVec6() {}

    public static FiguraVec6 get() {
        FiguraVec6 result = pool.poll();
        if (result == null)
            result = new FiguraVec6();
        else
            result.clear();
        return result;
    }

    public static FiguraVec6 get(double x, double y, double z, double w, double t, double h) {
        FiguraVec6 result = get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        result.h = h;
        return result;
    }

    private void clear() {
        x = y = z = w = t = h = 0;
    }

    public void copyFrom(FiguraVec6 other) {
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

    public FiguraVec6 plus(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        result.t += other.t;
        result.h += other.h;
        return result;
    }

    public FiguraVec6 minus(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        result.t -= other.t;
        result.h -= other.h;
        return result;
    }

    public FiguraVec6 times(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        result.t *= other.t;
        result.h *= other.h;
        return result;
    }

    public FiguraVec6 dividedBy(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        result.t /= other.t;
        result.h /= other.h;
        return result;
    }

    public FiguraVec6 mod(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        result.t %= other.t;
        result.h %= other.h;
        return result;
    }

    public FiguraVec6 iDividedBy(FiguraVec6 other) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        result.t = Math.floor(result.t / other.t);
        result.h = Math.floor(result.h / other.h);
        return result;
    }

    public FiguraVec6 toThePowerOf(double power) {
        FiguraVec6 result = get();
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

    public FiguraVec6 scaled(double factor) {
        FiguraVec6 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        result.t *= factor;
        result.h *= factor;
        return result;
    }

    //Mutators




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

    public static FiguraVec6 __add(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.plus(vec2);
    }

    public static FiguraVec6 __sub(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.minus(vec2);
    }

    public static FiguraVec6 __mul(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.times(vec2);
    }

    public static FiguraVec6 __mul(FiguraVec6 vec, double factor) {
        return vec.scaled(factor);
    }

    public static FiguraVec6 __mul(double factor, FiguraVec6 vec) {
        return vec.scaled(factor);
    }

    public static FiguraVec6 __div(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.dividedBy(vec2);
    }

    public static FiguraVec6 __div(FiguraVec6 vec, double factor) {
        return vec.scaled(1/factor);
    }

    public static FiguraVec6 __unm(FiguraVec6 vec) {
        return vec.scaled(-1);
    }

    public static FiguraVec6 __mod(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.mod(vec2);
    }

    public static FiguraVec6 __idiv(FiguraVec6 vec1, FiguraVec6 vec2) {
        return vec1.iDividedBy(vec2);
    }

    public static FiguraVec6 __pow(FiguraVec6 vec, double power) {
        return vec.toThePowerOf(power);
    }

    public static int __len(FiguraVec6 vec) {
        return 6;
    }

    public static double __call(FiguraVec6 vec) {
        return vec.getLength();
    }

}
