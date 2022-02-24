package net.blancworks.figura.avatar.script.api.math;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.*;

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

}
