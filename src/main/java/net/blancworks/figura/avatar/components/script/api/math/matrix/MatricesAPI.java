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
    public LuaMatrix4 mat(LuaVec4 col1, LuaVec4 col2, LuaVec4 col3, LuaVec4 col4) {
        return mat(
                col1.x, col1.y, col1.z, col1.w,
                col2.x, col2.y, col2.z, col2.w,
                col3.x, col3.y, col3.z, col3.w,
                col4.x, col4.y, col4.z, col4.w
        );
    }

    @LuaWhitelist
    public LuaMatrix2 identity2() {
        LuaMatrix2 result = LuaMatrix2.get();
        result.v11 = 1;
        result.v22 = 1;
        return result;
    }

    @LuaWhitelist
    public LuaMatrix3 identity3() {
        LuaMatrix3 result = LuaMatrix3.get();
        result.v11 = 1;
        result.v22 = 1;
        result.v33 = 1;
        return result;
    }

    @LuaWhitelist
    public LuaMatrix4 identity4() {
        LuaMatrix4 result = LuaMatrix4.get();
        result.v11 = 1;
        result.v22 = 1;
        result.v33 = 1;
        result.v44 = 1;
        return result;
    }

    //TODO: additional methods like scaleMatrix3, scaleMatrix2, etc.

    public LuaMatrix4 scaleMatrix(double x, double y, double z) {
        LuaMatrix4 result = LuaMatrix4.get();
        result.v11 = x;
        result.v22 = y;
        result.v33 = z;
        result.v44 = 1;
        return result;
    }

    public LuaMatrix4 scaleMatrix(LuaVec3 vec) {
        return scaleMatrix(vec.x, vec.y, vec.z);
    }

    public LuaMatrix4 xRotationMatrix(double radians) {
        double s = Math.sin(radians);
        double c = Math.cos(radians);
        LuaMatrix4 result = scaleMatrix(1, c, c);
        result.v23 = -s;
        result.v32 = s;
        return result;
    }

    public LuaMatrix4 yRotationMatrix(double radians) {
        double s = Math.sin(radians);
        double c = Math.cos(radians);
        LuaMatrix4 result = scaleMatrix(c, 1, c);
        result.v13 = s;
        result.v31 = -s;
        return result;
    }

    public LuaMatrix4 zRotationMatrix(double radians) {
        double s = Math.sin(radians);
        double c = Math.cos(radians);
        LuaMatrix4 result = scaleMatrix(c, c, 1);
        result.v12 = -s;
        result.v21 = s;
        return result;
    }

    public LuaMatrix4 xyzRotationMatrix(double x, double y, double z) {
//        double cx = Math.cos(x);
//        double cy = Math.cos(y);
//        double cz = Math.cos(z);
//        double sx = Math.sin(x);
//        double sy = Math.sin(y);
//        double sz = Math.sin(z);
        //TODO: make this more efficient by combining matrices manually with above numbers
        return zRotationMatrix(z).multiply(yRotationMatrix(y)).multiply(xRotationMatrix(x));
    }

    public LuaMatrix4 translate(double x, double y, double z) {
        LuaMatrix4 result = LuaMatrix4.get();
        result.v14 = x;
        result.v24 = y;
        result.v34 = z;
        result.v44 = 1;
        return result;
    }

    public LuaMatrix4 translate(LuaVec3 vec) {
        return translate(vec.x, vec.y, vec.z);
    }


}
