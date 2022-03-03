package net.blancworks.figura.avatar.script.api.math;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.matrix.FiguraMat2;
import net.blancworks.figura.utils.math.matrix.FiguraMat3;
import net.blancworks.figura.utils.math.matrix.FiguraMat4;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.blancworks.figura.utils.math.vector.FiguraVec4;

public class MatricesAPI extends ObjectWrapper<MatricesAPI> {


    @LuaWhitelist
    public FiguraMat3 mat(double v11, double v21, double v31,
                          double v12, double v22, double v32,
                          double v13, double v23, double v33) {
        FiguraMat3 result = FiguraMat3.get();
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
    public FiguraMat4 mat(double v11, double v21, double v31, double v41,
                          double v12, double v22, double v32, double v42,
                          double v13, double v23, double v33, double v43,
                          double v14, double v24, double v34, double v44) {
        FiguraMat4 result = FiguraMat4.get();
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
    public FiguraMat3 mat(FiguraVec3 col1, FiguraVec3 col2, FiguraVec3 col3) {
        return mat(
                col1.x, col1.y, col1.z,
                col2.x, col2.y, col2.z,
                col3.x, col3.y, col3.z
        );
    }

    @LuaWhitelist
    public FiguraMat4 mat(FiguraVec4 col1, FiguraVec4 col2, FiguraVec4 col3, FiguraVec4 col4) {
        return mat(
                col1.x, col1.y, col1.z, col1.w,
                col2.x, col2.y, col2.z, col2.w,
                col3.x, col3.y, col3.z, col3.w,
                col4.x, col4.y, col4.z, col4.w
        );
    }

    @LuaWhitelist
    public FiguraMat2 identity2() {
        FiguraMat2 result = FiguraMat2.get();
        result.v11 = 1;
        result.v22 = 1;
        return result;
    }

    @LuaWhitelist
    public FiguraMat3 identity3() {
        FiguraMat3 result = FiguraMat3.get();
        result.v11 = 1;
        result.v22 = 1;
        result.v33 = 1;
        return result;
    }

    @LuaWhitelist
    public FiguraMat4 identity4() {
        FiguraMat4 result = FiguraMat4.get();
        result.v11 = 1;
        result.v22 = 1;
        result.v33 = 1;
        result.v44 = 1;
        return result;
    }

    //TODO: additional methods like scale3, scale2, etc. for matrices of different sizes

    @LuaWhitelist
    public FiguraMat4 scale4(double x, double y, double z) {
        return FiguraMat4.createScaleMatrix(x, y, z);
    }

    @LuaWhitelist
    public FiguraMat4 scale4(FiguraVec3 vec) {
        return FiguraMat4.createScaleMatrix(vec.x, vec.y, vec.z);
    }

    @LuaWhitelist
    public FiguraMat4 xRotation4(double degrees) {
        return FiguraMat4.createXRotationMatrix(degrees);
    }

    @LuaWhitelist
    public FiguraMat4 yRotation4(double degrees) {
        return FiguraMat4.createYRotationMatrix(degrees);
    }

    @LuaWhitelist
    public FiguraMat4 zRotation4(double degrees) {
        return FiguraMat4.createZRotationMatrix(degrees);
    }

    @LuaWhitelist
    public FiguraMat4 zyxRotation4(double x, double y, double z) {
        return FiguraMat4.createZYXRotationMatrix(x, y, z);
    }

    @LuaWhitelist
    public FiguraMat4 zyxRotation4(FiguraVec3 vec) {
        return FiguraMat4.createZYXRotationMatrix(vec.x, vec.y, vec.z);
    }

    @LuaWhitelist
    public FiguraMat4 translate4(double x, double y, double z) {
        return FiguraMat4.createTranslationMatrix(x, y, z);
    }

    @LuaWhitelist
    public FiguraMat4 translate4(FiguraVec3 vec) {
        return FiguraMat4.createTranslationMatrix(vec.x, vec.y, vec.z);
    }


}
