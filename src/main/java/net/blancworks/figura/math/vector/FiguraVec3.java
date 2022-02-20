package net.blancworks.figura.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.matrix.FiguraMat3;
import net.minecraft.util.math.Vec3f;

import java.util.LinkedList;
import java.util.Queue;

public class FiguraVec3 extends ObjectWrapper<FiguraVec3> {

    @LuaWhitelist
    public double x, y, z;

    private static final Queue<FiguraVec3> pool = new LinkedList<>();

    public static final FiguraVec3 ONE = get(1, 1, 1);

    private FiguraVec3() {}

    public static FiguraVec3 get() {
        FiguraVec3 result = pool.poll();
        if (result == null)
            result = new FiguraVec3();
        else
            result.clear();
        return result;
    }

    public static FiguraVec3 get(double x, double y, double z) {
        FiguraVec3 result = get();
        result.x = x;
        result.y = y;
        result.z = z;
        return result;
    }

    private void clear() {
        x = y = z = 0;
    }

    @LuaWhitelist
    public FiguraVec3 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(FiguraVec3 other) {
        x = other.x;
        y = other.y;
        z = other.z;
    }

    public void copyFrom(Vec3f other) {
        x = other.getX();
        y = other.getY();
        z = other.getZ();
    }

    public static FiguraVec3 of(Vec3f vec) {
        FiguraVec3 result = get();
        result.x = vec.getX();
        result.y = vec.getY();
        result.z = vec.getZ();
        return result;
    }

    public String toString() {
        return String.format("vec3:{%f, %f, %f}", x, y, z);
    }

    public FiguraVec3 plus(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        result.z += other.z;
        return result;
    }

    public FiguraVec3 minus(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        result.z -= other.z;
        return result;
    }

    public FiguraVec3 times(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        result.z *= other.z;
        return result;
    }

    public FiguraVec3 dividedBy(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        result.z /= other.z;
        return result;
    }

    public FiguraVec3 mod(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        result.z %= other.z;
        return result;
    }

    public FiguraVec3 iDividedBy(FiguraVec3 other) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        result.z = Math.floor(result.z / other.z);
        return result;
    }

    public FiguraVec3 toThePowerOf(double power) {
        FiguraVec3 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        result.z = Math.pow(z, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public FiguraVec3 scaled(double factor) {
        FiguraVec3 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        result.z *= factor;
        return result;
    }

    @LuaWhitelist
    public FiguraVec4 augment() {
        FiguraVec4 result = FiguraVec4.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = 1;
        return result;
    }

    //Mutators

    public void multiply(FiguraMat3 mat) {
        double nx = mat.v11*x+mat.v12*y+mat.v13*z;
        double ny = mat.v21*x+mat.v22*y+mat.v23*z;
        z = mat.v31*x+mat.v32*y+mat.v33*z;
        x = nx;
        y = ny;
    }




    //Lua interactions

    public static FiguraVec3 __add(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.plus(vec2);
    }

    public static FiguraVec3 __sub(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.minus(vec2);
    }

    public static FiguraVec3 __mul(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.times(vec2);
    }

    public static FiguraVec3 __mul(FiguraVec3 vec, double factor) {
        return vec.scaled(factor);
    }

    public static FiguraVec3 __mul(double factor, FiguraVec3 vec) {
        return vec.scaled(factor);
    }

    public static FiguraVec3 __div(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.dividedBy(vec2);
    }

    public static FiguraVec3 __div(FiguraVec3 vec, double factor) {
        return vec.scaled(1/factor);
    }

    public static FiguraVec3 __unm(FiguraVec3 vec) {
        return vec.scaled(-1);
    }

    public static FiguraVec3 __mod(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.mod(vec2);
    }

    public static FiguraVec3 __idiv(FiguraVec3 vec1, FiguraVec3 vec2) {
        return vec1.iDividedBy(vec2);
    }

    public static FiguraVec3 __pow(FiguraVec3 vec, double power) {
        return vec.toThePowerOf(power);
    }

    public static int __len(FiguraVec3 vec) {
        return 4;
    }

    public static double __call(FiguraVec3 vec) {
        return vec.getLength();
    }

}
