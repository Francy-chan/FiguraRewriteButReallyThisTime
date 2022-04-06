package net.blancworks.figura.utils.math.vector;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.MathUtils;
import net.blancworks.figura.utils.math.matrix.FiguraMat2;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

public class FiguraVec2 extends ObjectWrapper<FiguraVec2> {

    @LuaWhitelist
    public double x, y;

    private static final Queue<FiguraVec2> pool = new LinkedList<>();

    private FiguraVec2() {}

    public static FiguraVec2 get() {
        FiguraVec2 result = pool.poll();
        if (result == null)
            result = new FiguraVec2();
        else
            result.clear();
        return result;
    }

    public static FiguraVec2 get(double x, double y) {
        FiguraVec2 result = get();
        result.x = x;
        result.y = y;
        return result;
    }

    private void clear() {
        x = y = 0;
    }

    public void copyFrom(FiguraVec2 other) {
        x = other.x;
        y = other.y;
    }

    public String toString() {
        return String.format("vec2:{%f, %f}", x, y);
    }

    public FiguraVec2 plus(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        return result;
    }

    public FiguraVec2 minus(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        return result;
    }

    public FiguraVec2 times(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        return result;
    }

    public FiguraVec2 dividedBy(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        return result;
    }

    public FiguraVec2 mod(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        return result;
    }

    public FiguraVec2 iDividedBy(FiguraVec2 other) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        return result;
    }

    public FiguraVec2 toThePowerOf(double power) {
        FiguraVec2 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y);
    }

    public FiguraVec2 scale(double factor) {
        FiguraVec2 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        return result;
    }

    public FiguraVec2 times(FiguraMat2 mat) {
        FiguraVec2 result = get();
        result.x = mat.v11*x+mat.v12*y;
        result.y = mat.v21*x+mat.v22*y;
        return result;
    }

    //Lua interactions

    public static FiguraVec2 __add(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.plus(vec2);
    }

    public static FiguraVec2 __sub(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.minus(vec2);
    }

    public static FiguraVec2 __mul(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.times(vec2);
    }

    public static FiguraVec2 __mul(FiguraVec2 vec, double factor) {
        return vec.scale(factor);
    }

    public static FiguraVec2 __mul(double factor, FiguraVec2 vec) {
        return vec.scale(factor);
    }

    public static FiguraVec2 __div(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.dividedBy(vec2);
    }

    public static FiguraVec2 __div(FiguraVec2 vec, double factor) {
        return vec.scale(1/factor);
    }

    public static FiguraVec2 __unm(FiguraVec2 vec) {
        return vec.scale(-1);
    }

    public static FiguraVec2 __mod(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.mod(vec2);
    }

    public static FiguraVec2 __idiv(FiguraVec2 vec1, FiguraVec2 vec2) {
        return vec1.iDividedBy(vec2);
    }

    public static FiguraVec2 __pow(FiguraVec2 vec, double power) {
        return vec.toThePowerOf(power);
    }

    public static int __len(FiguraVec2 vec) {
        return 4;
    }

    public static double __call(FiguraVec2 vec) {
        return vec.getLength();
    }

    @Override
    public Object getFallback(String key) {
        int len = key.length();
        if (len == 1) return switch(key) {
            case "1", "r" -> x;
            case "2", "g" -> y;
            default -> null;
        };

        double[] vals = new double[len];
        for (int i = 0; i < len; i++)
            vals[i] = switch (key.charAt(i)) {
                case '1', 'x', 'r' -> x;
                case '2', 'y', 'g' -> y;
                case '_' -> 0;
                default -> throw new IllegalArgumentException("Invalid swizzle: " + key);
            };
        return MathUtils.sizedVector(len, vals);
    }

}
