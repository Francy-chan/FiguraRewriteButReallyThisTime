package net.blancworks.figura.avatar.components.script.api.math.vector;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

public class VectorsAPI extends ObjectWrapper<VectorsAPI> {

    @LuaWhitelist
    public LuaVec2 vec(double x, double y) {
        LuaVec2 result = LuaVec2.get();
        result.x = x;
        result.y = y;
        return result;
    }

    @LuaWhitelist
    public LuaVec3 vec(double x, double y, double z) {
        LuaVec3 result = LuaVec3.get();
        result.x = x;
        result.y = y;
        result.z = z;
        return result;
    }

    @LuaWhitelist
    public LuaVec4 vec(double x, double y, double z, double w) {
        LuaVec4 result = LuaVec4.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        return result;
    }

    @LuaWhitelist
    public LuaVec5 vec(double x, double y, double z, double w, double t) {
        LuaVec5 result = LuaVec5.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        return result;
    }

    @LuaWhitelist
    public LuaVec6 vec(double x, double y, double z, double w, double t, double h) {
        LuaVec6 result = LuaVec6.get();
        result.x = x;
        result.y = y;
        result.z = z;
        result.w = w;
        result.t = t;
        result.h = h;
        return result;
    }

}
