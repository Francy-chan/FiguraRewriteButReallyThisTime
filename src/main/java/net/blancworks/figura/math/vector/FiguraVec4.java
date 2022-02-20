package net.blancworks.figura.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.matrix.FiguraMat4;

import java.util.LinkedList;
import java.util.Queue;

public class FiguraVec4 extends ObjectWrapper<FiguraVec4> {

    public static final FiguraVec4 ONE = FiguraVec4.get(1, 1, 1, 1);

    @LuaWhitelist
    public double x, y, z, w;

    private static final Queue<FiguraVec4> pool = new LinkedList<>();

    private FiguraVec4() {}

    public static FiguraVec4 get() {
        FiguraVec4 result = pool.poll();
        if (result == null)
            result = new FiguraVec4();
        else
            result.clear();
        return result;
    }

    public static FiguraVec4 get(double x, double y, double z, double w) {
        FiguraVec4 result = get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        return result;
    }

    private void clear() {
        x = y = z = w = 0;
    }

    @LuaWhitelist
    public FiguraVec4 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(FiguraVec4 other) {
        x = other.x;
        y = other.y;
        z = other.z;
        w = other.w;
    }

    public String toString() {
        return String.format("vec4:{%f, %f, %f, %f}", x, y, z, w);
    }

    public FiguraVec4 plus(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        result.w += other.w;
        return result;
    }

    public FiguraVec4 minus(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        result.w -= other.w;
        return result;
    }

    public FiguraVec4 times(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        result.w *= other.w;
        return result;
    }

    public FiguraVec4 dividedBy(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        result.w /= other.w;
        return result;
    }

    public FiguraVec4 mod(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        result.w %= other.w;
        return result;
    }

    public FiguraVec4 iDividedBy(FiguraVec4 other) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        result.w = Math.floor(result.w / other.w);
        return result;
    }

    public FiguraVec4 toThePowerOf(double power) {
        FiguraVec4 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        result.w = Math.pow(w, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y+z*z+w*w);
    }

    public FiguraVec4 scaled(double factor) {
        FiguraVec4 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        result.w *= factor;
        return result;
    }

    //Mutators

    public void multiply(FiguraMat4 mat) {
        double nx = mat.v11*x+mat.v12*y+mat.v13*z+mat.v14*w;
        double ny = mat.v21*x+mat.v22*y+mat.v23*z+mat.v24*w;
        double nz = mat.v31*x+mat.v32*y+mat.v33*z+mat.v34*w;
        w = mat.v41*x+mat.v42*y+mat.v43*z+mat.v44*w;
        x = nx;
        y = ny;
        z = nz;
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

    public static FiguraVec4 __add(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.plus(vec2);
    }

    public static FiguraVec4 __sub(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.minus(vec2);
    }

    public static FiguraVec4 __mul(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.times(vec2);
    }

    public static FiguraVec4 __mul(FiguraVec4 vec, double factor) {
        return vec.scaled(factor);
    }

    public static FiguraVec4 __mul(double factor, FiguraVec4 vec) {
        return vec.scaled(factor);
    }

    public static FiguraVec4 __div(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.dividedBy(vec2);
    }

    public static FiguraVec4 __div(FiguraVec4 vec, double factor) {
        return vec.scaled(1/factor);
    }

    public static FiguraVec4 __unm(FiguraVec4 vec) {
        return vec.scaled(-1);
    }

    public static FiguraVec4 __mod(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.mod(vec2);
    }

    public static FiguraVec4 __idiv(FiguraVec4 vec1, FiguraVec4 vec2) {
        return vec1.iDividedBy(vec2);
    }

    public static FiguraVec4 __pow(FiguraVec4 vec, double power) {
        return vec.toThePowerOf(power);
    }

    public static int __len(FiguraVec4 vec) {
        return 4;
    }

    public static double __call(FiguraVec4 vec) {
        return vec.getLength();
    }

}
