package net.blancworks.figura.avatar.components.script.api.math.matrix;

import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec3;
import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec4;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

public class MatricesAPI extends ObjectWrapper<MatricesAPI> {

    @LuaWhitelist
    public LuaMatrix3 mat(double v11, double v21, double v31,
                          double v12, double v22, double v32,
                          double v13, double v23, double v33) {
        LuaMatrix3 result = LuaMatrix3.get();
        result.v11 = v11;
        result.v12 = v12;
        result.v13 = v13;
        result.v21 = v21;
        result.v22 = v22;
        result.v23 = v23;
        result.v31 = v31;
        result.v32 = v32;
        result.v33 = v33;
        return result;
    }

    @LuaWhitelist
    public LuaMatrix3x4 mat(double v11, double v21, double v31,
                          double v12, double v22, double v32,
                          double v13, double v23, double v33,
                          double v14, double v24, double v34) {
        LuaMatrix3x4 result = LuaMatrix3x4.get();
        result.v11 = v11;
        result.v12 = v12;
        result.v13 = v13;
        result.v14 = v14;
        result.v21 = v21;
        result.v22 = v22;
        result.v23 = v23;
        result.v24 = v24;
        result.v31 = v31;
        result.v32 = v32;
        result.v33 = v33;
        result.v34 = v34;
        return result;
    }

    @LuaWhitelist
    public LuaMatrix4 mat(double v11, double v21, double v31, double v41,
                            double v12, double v22, double v32, double v42,
                            double v13, double v23, double v33, double v43,
                            double v14, double v24, double v34, double v44) {
        LuaMatrix4 result = LuaMatrix4.get();
        result.v11 = v11;
        result.v12 = v12;
        result.v13 = v13;
        result.v14 = v14;
        result.v21 = v21;
        result.v22 = v22;
        result.v23 = v23;
        result.v24 = v24;
        result.v31 = v31;
        result.v32 = v32;
        result.v33 = v33;
        result.v34 = v34;
        result.v41 = v41;
        result.v42 = v42;
        result.v43 = v43;
        result.v44 = v44;
        return result;
    }

    @LuaWhitelist
    public LuaMatrix3 mat(LuaVec3 col1, LuaVec3 col2, LuaVec3 col3) {
        return mat(
                col1.x, col1.y, col1.z,
                col2.x, col2.y, col2.z,
                col3.x, col3.y, col3.z
        );
    }

    @LuaWhitelist
    public LuaMatrix3x4 mat(LuaVec3 col1, LuaVec3 col2, LuaVec3 col3, LuaVec3 col4) {
        return mat(
                col1.x, col1.y, col1.z,
                col2.x, col2.y, col2.z,
                col3.x, col3.y, col3.z,
                col4.x, col4.y, col4.z
        );
    }

    @LuaWhitelist
    public LuaMatrix4 mat(LuaVec4 col1, LuaVec4 col2, LuaVec4 col3, LuaVec4 col4) {
        return mat(
                col1.x, col1.y, col1.z, col1.w,
                col2.x, col2.y, col2.z, col2.w,
                col3.x, col3.y, col3.z, col3.w,
                col4.x, col4.y, col4.z, col4.w
        );
    }

}
