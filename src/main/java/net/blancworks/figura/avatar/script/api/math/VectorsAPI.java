package net.blancworks.figura.avatar.script.api.math;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.*;
import net.minecraft.util.math.MathHelper;

public class VectorsAPI extends ObjectWrapper<VectorsAPI> {

    @LuaWhitelist
    public FiguraVec2 vec(double x, double y) {
        FiguraVec2 result = FiguraVec2.get();
        result.x = x;
        result.y = y;
        return result;
    }

    @LuaWhitelist
    public FiguraVec3 vec(double x, double y, double z) {
        FiguraVec3 result = FiguraVec3.get();
        result.x = x;
        result.y = y;
        result.z = z;
        return result;
    }

    @LuaWhitelist
    public FiguraVec4 vec(double x, double y, double z, double w) {
        FiguraVec4 result = FiguraVec4.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        return result;
    }

    @LuaWhitelist
    public FiguraVec5 vec(double x, double y, double z, double w, double t) {
        FiguraVec5 result = FiguraVec5.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        return result;
    }

    @LuaWhitelist
    public FiguraVec6 vec(double x, double y, double z, double w, double t, double h) {
        FiguraVec6 result = FiguraVec6.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        result.h = h;
        return result;
    }

    @LuaWhitelist
    public int rgbToInt(FiguraVec4 vec4) {
        return IntFromRGB(vec4);
    }

    @LuaWhitelist
    public FiguraVec4 intToRgb(int value) {
        return RGBFromInt(value);
    }

    public static FiguraVec4 RGBFromInt(int colorInt) {
        FiguraVec4 v = FiguraVec4.get();
        v.w = (colorInt) & 0xFF;
        v.z = (colorInt >> 8) & 0xFF;
        v.y = (colorInt >> 16) & 0xFF;
        v.x = (colorInt >> 24) & 0xFF;
        return v;
    }

    public static int IntFromRGB(FiguraVec4 vec) {
        int intColor = MathHelper.clamp((int) vec.x, 0, 255);
        intColor = (intColor << 8) + MathHelper.clamp((int) vec.y, 0, 255);
        intColor = (intColor << 8) + MathHelper.clamp((int) vec.z, 0, 255);
        intColor = (intColor << 8) + MathHelper.clamp((int) vec.w, 0, 255);
        return intColor;
    }

}
