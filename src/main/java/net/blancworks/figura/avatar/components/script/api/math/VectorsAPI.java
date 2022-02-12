package net.blancworks.figura.avatar.components.script.api.math;

import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec6;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

public class VectorsAPI extends ObjectWrapper<VectorsAPI> {

    @LuaWhitelist
    public LuaVec6 vec(double x, double y, double z) {
        LuaVec6 result = LuaVec6.get();
        result.x = x;
        result.y = y;
        result.z = z;
        return result;
    }

}
