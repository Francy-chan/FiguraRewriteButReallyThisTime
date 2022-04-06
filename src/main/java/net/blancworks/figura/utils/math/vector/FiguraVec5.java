package net.blancworks.figura.utils.math.vector;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.MathUtils;

import java.util.LinkedList;
import java.util.Queue;

public class FiguraVec5 extends ObjectWrapper<FiguraVec5> {

    @LuaWhitelist
    public double x, y, z, w, t;

    private static final Queue<FiguraVec5> pool = new LinkedList<>();

    private FiguraVec5() {}

    public static FiguraVec5 get() {
        FiguraVec5 result = pool.poll();
        if (result == null)
            result = new FiguraVec5();
        else
            result.clear();
        return result;
    }

    public static FiguraVec5 get(double x, double y, double z, double w, double t) {
        FiguraVec5 result = get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        return result;
    }

    private void clear() {
        x = y = z = w = t = 0;
    }

    public void copyFrom(FiguraVec5 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
        t = other.t;
    }

    public String toString() {
        return String.format("vec5:{%f, %f, %f, %f, %f}", x, y, z, w, t);
    }

    public FiguraVec5 plus(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        result.t += other.t;
        return result;
    }

    public FiguraVec5 minus(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        result.t -= other.t;
        return result;
    }

    public FiguraVec5 times(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        result.t *= other.t;
        return result;
    }

    public FiguraVec5 dividedBy(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        result.t /= other.t;
        return result;
    }

    public FiguraVec5 mod(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        result.t %= other.t;
        return result;
    }

    public FiguraVec5 iDividedBy(FiguraVec5 other) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        result.t = Math.floor(result.t / other.t);
        return result;
    }

    public FiguraVec5 toThePowerOf(double power) {
        FiguraVec5 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        result.w = Math.pow(w, power);
        result.t = Math.pow(t, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y+z*z+w*w+t*t);
    }

    public FiguraVec5 scaled(double factor) {
        FiguraVec5 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        result.t *= factor;
        return result;
    }

    //Mutators



    //Lua interaction

    public static FiguraVec5 __add(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.plus(vec2);
    }

    public static FiguraVec5 __sub(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.minus(vec2);
    }

    public static FiguraVec5 __mul(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.times(vec2);
    }

    public static FiguraVec5 __mul(FiguraVec5 vec, double factor) {
        return vec.scaled(factor);
    }

    public static FiguraVec5 __mul(double factor, FiguraVec5 vec) {
        return vec.scaled(factor);
    }

    public static FiguraVec5 __div(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.dividedBy(vec2);
    }

    public static FiguraVec5 __div(FiguraVec5 vec, double factor) {
        return vec.scaled(1/factor);
    }

    public static FiguraVec5 __unm(FiguraVec5 vec) {
        return vec.scaled(-1);
    }

    public static FiguraVec5 __mod(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.mod(vec2);
    }

    public static FiguraVec5 __idiv(FiguraVec5 vec1, FiguraVec5 vec2) {
        return vec1.iDividedBy(vec2);
    }

    public static FiguraVec5 __pow(FiguraVec5 vec, double power) {
        return vec.toThePowerOf(power);
    }

    public static int __len(FiguraVec5 vec) {
        return 5;
    }

    public static double __call(FiguraVec5 vec) {
        return vec.getLength();
    }

    @Override
    public Object getFallback(String key) {
        int len = key.length();
        if (len == 1) return switch(key) {
            case "1", "r" -> x;
            case "2", "g" -> y;
            case "3", "b" -> z;
            case "4", "a" -> w;
            case "5" -> t;
            default -> null;
        };

        double[] vals = new double[len];
        for (int i = 0; i < len; i++)
            vals[i] = switch (key.charAt(i)) {
                case '1', 'x', 'r' -> x;
                case '2', 'y', 'g' -> y;
                case '3', 'z', 'b' -> z;
                case '4', 'w', 'a' -> w;
                case '5', 't' -> t;
                case '_' -> 0;
                default -> throw new IllegalArgumentException("Invalid swizzle: " + key);
            };
        return MathUtils.sizedVector(len, vals);
    }

}
