package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix2;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

import java.util.LinkedList;
import java.util.Queue;

public class LuaVec2 extends ObjectWrapper<LuaVec2> {

    @LuaWhitelist
    public double x, y;

    private static final Queue<LuaVec2> pool = new LinkedList<>();

    private LuaVec2() {}

    public static LuaVec2 get() {
        LuaVec2 result = pool.poll();
        if (result == null)
            result = new LuaVec2();
        else
            result.clear();
        return result;
    }

    private void clear() {
        x = y = 0;
    }

    @LuaWhitelist
    public LuaVec2 free() {
        pool.add(this);
        return this;
    }

    public void copyFrom(LuaVec2 other) {
        x = other.x;
        y = other.y;
    }

    public String toString() {
        return String.format("vec2:{%f, %f}", x, y);
    }

    public LuaVec2 add(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x += other.x;
        result.y += other.y;
        return result;
    }

    public LuaVec2 sub(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x -= other.x;
        result.y -= other.y;
        return result;
    }

    public LuaVec2 mul(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x *= other.x;
        result.y *= other.y;
        return result;
    }

    public LuaVec2 div(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x /= other.x;
        result.y /= other.y;
        return result;
    }

    public LuaVec2 mod(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x %= other.x;
        result.y %= other.y;
        return result;
    }

    public LuaVec2 idiv(LuaVec2 other) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x = Math.floor(result.x / other.x);
        result.y = Math.floor(result.y / other.y);
        return result;
    }

    public LuaVec2 pow(double power) {
        LuaVec2 result = get();
        result.x = Math.pow(x, power);
        result.y = Math.pow(y, power);
        return result;
    }

    @LuaWhitelist
    public double getLength() {
        return Math.sqrt(x*x+y*y);
    }

    public LuaVec2 scale(double factor) {
        LuaVec2 result = get();
        result.copyFrom(this);
        result.x *= factor;
        result.y *= factor;
        return result;
    }

    public LuaVec2 multiply(LuaMatrix2 mat) {
        LuaVec2 result = get();
        result.x = mat.v11*x+mat.v12*y;
        result.y = mat.v21*x+mat.v22*y;
        return result;
    }

    //Lua interactions

    public static LuaVec2 __add(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.add(vec2);
    }

    public static LuaVec2 __sub(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.sub(vec2);
    }

    public static LuaVec2 __mul(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.mul(vec2);
    }

    public static LuaVec2 __mul(LuaVec2 vec, double factor) {
        return vec.scale(factor);
    }

    public static LuaVec2 __mul(double factor, LuaVec2 vec) {
        return vec.scale(factor);
    }

    public static LuaVec2 __div(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.div(vec2);
    }

    public static LuaVec2 __div(LuaVec2 vec, double factor) {
        return vec.scale(1/factor);
    }

    public static LuaVec2 __unm(LuaVec2 vec) {
        return vec.scale(-1);
    }

    public static LuaVec2 __mod(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.mod(vec2);
    }

    public static LuaVec2 __idiv(LuaVec2 vec1, LuaVec2 vec2) {
        return vec1.idiv(vec2);
    }

    public static LuaVec2 __pow(LuaVec2 vec, double power) {
        return vec.pow(power);
    }

    public static int __len(LuaVec2 vec) {
        return 4;
    }

    public static double __call(LuaVec2 vec) {
        return vec.getLength();
    }

}
