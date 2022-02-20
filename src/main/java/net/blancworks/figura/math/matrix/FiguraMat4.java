package net.blancworks.figura.math.matrix;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec4;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class FiguraMat4 extends ObjectWrapper<FiguraMat4> {

    //values are row-column. So v14 is the top right corner of the matrix.
    //can I make these public? I don't want to deal with all the setters and getters aaaa
    @LuaWhitelist
    public double v11 = 1, v12, v13, v14, v21, v22 = 1, v23, v24, v31, v32, v33 = 1, v34, v41, v42, v43, v44 = 1;

    private static Queue<FiguraMat4> pool = new LinkedList<>();

    private FiguraMat4() {}

    /**
     * Gets a matrix, set to the identity matrix.
     * @return
     */
    public static FiguraMat4 get() {
        FiguraMat4 result = pool.poll();
        if (result == null)
            result = new FiguraMat4();
        else
            result.resetToIdentity();
        return result;
    }

    @LuaWhitelist
    public void resetToIdentity() {
        v11 = v22 = v33 = v44 = 1;
        v12 = v13 = v14 = v21 = v23 = v24 = v31 = v32 = v34 = v41 = v42 = v43 = 0;
    }

    @LuaWhitelist
    public FiguraMat4 free() {
        pool.add(this);
        return this;
    }

    //Static matrix creator methods

    public static FiguraMat4 createScaleMatrix(double x, double y, double z) {
        FiguraMat4 result = get();
        result.v11 = x;
        result.v22 = y;
        result.v33 = z;
        return result;
    }

    public static FiguraMat4 createXRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat4 result = get();
        result.v22 = result.v33 = c;
        result.v23 = -s;
        result.v32 = s;
        return result;
    }

    public static FiguraMat4 createYRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat4 result = get();
        result.v11 = result.v33 = c;
        result.v13 = s;
        result.v31 = -s;
        return result;
    }

    public static FiguraMat4 createZRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat4 result = get();
        result.v11 = result.v22 = c;
        result.v12 = -s;
        result.v21 = s;
        return result;
    }

    public static FiguraMat4 createZYXRotationMatrix(double x, double y, double z) {
        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);

        double a = Math.cos(x);
        double b = Math.sin(x);
        double c = Math.cos(y);
        double d = Math.sin(y);
        double e = Math.cos(z);
        double f = Math.sin(z);

        FiguraMat4 result = get();
        result.v11 = c*e;
        result.v12 = b*d*e - a*f;
        result.v13 = a*d*e + b*f;
        result.v21 = c*f;
        result.v22 = b*d*f + a*e;
        result.v23 = a*d*f - b*e;
        result.v31 = -d;
        result.v32 = b*c;
        result.v33 = a*c;
        return result;
    }

    public static FiguraMat4 createTranslationMatrix(double x, double y, double z) {
        FiguraMat4 result = get();
        result.v14 = x;
        result.v24 = y;
        result.v34 = z;
        return result;
    }

    // Mutation methods

    public void translate(double x, double y, double z) {
        v11 += x * v41;
        v12 += x * v42;
        v13 += x * v43;
        v14 += x * v44;

        v21 += y * v41;
        v22 += y * v42;
        v23 += y * v43;
        v24 += y * v44;

        v31 += z * v41;
        v32 += z * v42;
        v33 += z * v43;
        v34 += z * v44;
    }

    public void scale(double x, double y, double z) {
        v11 *= x;
        v12 *= x;
        v13 *= x;
        v14 *= x;
        v21 *= y;
        v22 *= y;
        v23 *= y;
        v24 *= y;
        v31 *= z;
        v32 *= z;
        v33 *= z;
        v34 *= z;
    }

    public void rotateX(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv21 = c*v21 - s*v31;
        double nv22 = c*v22 - s*v32;
        double nv23 = c*v23 - s*v33;
        double nv24 = c*v24 - s*v34;

        v31 = s*v21 + c*v31;
        v32 = s*v22 + c*v32;
        v33 = s*v23 + c*v33;
        v34 = s*v24 + c*v34;

        v21 = nv21;
        v22 = nv22;
        v23 = nv23;
        v24 = nv24;
    }

    public void rotateY(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv11 = c*v11 + s*v31;
        double nv12 = c*v12 + s*v32;
        double nv13 = c*v13 + s*v33;
        double nv14 = c*v14 + s*v34;

        v31 = c*v31 - s*v11;
        v32 = c*v32 - s*v12;
        v33 = c*v33 - s*v13;
        v34 = c*v34 - s*v14;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
        v14 = nv14;
    }

    public void rotateZ(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv11 = c*v11 - s*v21;
        double nv12 = c*v12 - s*v22;
        double nv13 = c*v13 - s*v23;
        double nv14 = c*v14 - s*v24;

        v21 = c*v21 + s*v11;
        v22 = c*v22 + s*v12;
        v23 = c*v23 + s*v13;
        v24 = c*v24 + s*v14;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
        v14 = nv14;
    }

    //Rotates using ZYX matrix order, meaning the X axis, then Y, then Z.
    public void rotateZYX(double x, double y, double z) {
        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);

        double a = Math.cos(x);
        double b = Math.sin(x);
        double c = Math.cos(y);
        double d = Math.sin(y);
        double e = Math.cos(z);
        double f = Math.sin(z);

        double bc = b*c;
        double ac = a*c;
        double ce = c*e;
        double cf = c*f;
        double p1 = (b*d*e - a*f);
        double p2 = (a*d*e + b*f);
        double p3 = (a*e + b*d*f);
        double p4 = (a*d*f - b*e);

        double nv11 = ce*v11 + p1*v21 + p2*v31;
        double nv21 = cf*v11 + p3*v21 + p4*v31;
        double nv31 = -d*v11 + bc*v21 + ac*v31;

        double nv12 = ce*v12 + p1*v22 + p2*v32;
        double nv22 = cf*v12 + p3*v22 + p4*v32;
        double nv32 = -d*v12 + bc*v22 + ac*v32;

        double nv13 = ce*v13 + p1*v23 + p2*v33;
        double nv23 = cf*v13 + p3*v23 + p4*v33;
        double nv33 = -d*v13 + bc*v23 + ac*v33;

        double nv14 = ce*v14 + p1*v24 + p2*v34;
        double nv24 = cf*v14 + p3*v24 + p4*v34;
        v34 = -d*v14 + bc*v24 + ac*v34;

        v11 = nv11;
        v21 = nv21;
        v31 = nv31;
        v12 = nv12;
        v22 = nv22;
        v32 = nv32;
        v13 = nv13;
        v23 = nv23;
        v33 = nv33;
        v14 = nv14;
        v24 = nv24;
    }

    public void multiply(FiguraMat4 o) {
        double nv11 = o.v11*v11+o.v12*v21+o.v13*v31+o.v14*v41;
        double nv12 = o.v11*v12+o.v12*v22+o.v13*v32+o.v14*v42;
        double nv13 = o.v11*v13+o.v12*v23+o.v13*v33+o.v14*v43;
        double nv14 = o.v11*v14+o.v12*v24+o.v13*v34+o.v14*v44;

        double nv21 = o.v21*v11+o.v22*v21+o.v23*v31+o.v24*v41;
        double nv22 = o.v21*v12+o.v22*v22+o.v23*v32+o.v24*v42;
        double nv23 = o.v21*v13+o.v22*v23+o.v23*v33+o.v24*v43;
        double nv24 = o.v21*v14+o.v22*v24+o.v23*v34+o.v24*v44;

        double nv31 = o.v31*v11+o.v32*v21+o.v33*v31+o.v34*v41;
        double nv32 = o.v31*v12+o.v32*v22+o.v33*v32+o.v34*v42;
        double nv33 = o.v31*v13+o.v32*v23+o.v33*v33+o.v34*v43;
        double nv34 = o.v31*v14+o.v32*v24+o.v33*v34+o.v34*v44;

        double nv41 = o.v41*v11+o.v42*v21+o.v43*v31+o.v44*v41;
        double nv42 = o.v41*v12+o.v42*v22+o.v43*v32+o.v44*v42;
        double nv43 = o.v41*v13+o.v42*v23+o.v43*v33+o.v44*v43;
        v44 = o.v41*v14+o.v42*v24+o.v43*v34+o.v44*v44;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
        v14 = nv14;
        v21 = nv21;
        v22 = nv22;
        v23 = nv23;
        v24 = nv24;
        v31 = nv31;
        v32 = nv32;
        v33 = nv33;
        v34 = nv34;
        v41 = nv41;
        v42 = nv42;
        v43 = nv43;
    }

    //TODO: methods for inverse, determinant, other common matrix operations

    @LuaWhitelist
    public void copyFrom(FiguraMat4 other) {
        v11 = other.v11;
        v12 = other.v12;
        v13 = other.v13;
        v14 = other.v14;
        v21 = other.v21;
        v22 = other.v22;
        v23 = other.v23;
        v24 = other.v24;
        v31 = other.v31;
        v32 = other.v32;
        v33 = other.v33;
        v34 = other.v34;
        v41 = other.v41;
        v42 = other.v42;
        v43 = other.v43;
        v44 = other.v44;
    }

    @LuaWhitelist
    public FiguraMat4 transpose() {
        FiguraMat4 result = get();
        result.v11 = v11;
        result.v12 = v21;
        result.v13 = v31;
        result.v14 = v41;
        result.v21 = v12;
        result.v22 = v22;
        result.v23 = v32;
        result.v24 = v42;
        result.v31 = v13;
        result.v32 = v23;
        result.v33 = v33;
        result.v34 = v43;
        result.v41 = v14;
        result.v42 = v24;
        result.v43 = v34;
        result.v44 = v44;
        return result;
    }

    //Returns the product of the matrices, with "o" on the left.
    public FiguraMat4 times(FiguraMat4 o) {
        FiguraMat4 result = get();

        result.v11 = o.v11*v11+o.v12*v21+o.v13*v31+o.v14*v41;
        result.v12 = o.v11*v12+o.v12*v22+o.v13*v32+o.v14*v42;
        result.v13 = o.v11*v13+o.v12*v23+o.v13*v33+o.v14*v43;
        result.v14 = o.v11*v14+o.v12*v24+o.v13*v34+o.v14*v44;

        result.v21 = o.v21*v11+o.v22*v21+o.v23*v31+o.v24*v41;
        result.v22 = o.v21*v12+o.v22*v22+o.v23*v32+o.v24*v42;
        result.v23 = o.v21*v13+o.v22*v23+o.v23*v33+o.v24*v43;
        result.v24 = o.v21*v14+o.v22*v24+o.v23*v34+o.v24*v44;

        result.v31 = o.v31*v11+o.v32*v21+o.v33*v31+o.v34*v41;
        result.v32 = o.v31*v12+o.v32*v22+o.v33*v32+o.v34*v42;
        result.v33 = o.v31*v13+o.v32*v23+o.v33*v33+o.v34*v43;
        result.v34 = o.v31*v14+o.v32*v24+o.v33*v34+o.v34*v44;

        result.v41 = o.v41*v11+o.v42*v21+o.v43*v31+o.v44*v41;
        result.v42 = o.v41*v12+o.v42*v22+o.v43*v32+o.v44*v42;
        result.v43 = o.v41*v13+o.v42*v23+o.v43*v33+o.v44*v43;
        result.v44 = o.v41*v14+o.v42*v24+o.v43*v34+o.v44*v44;

        return result;
    }

    public FiguraVec4 times(FiguraVec4 vec) {
        FiguraVec4 result = FiguraVec4.get();
        result.x = v11*vec.x+v12*vec.y+v13*vec.z+v14*vec.w;
        result.y = v21*vec.x+v22*vec.y+v23*vec.z+v24*vec.w;
        result.z = v31*vec.x+v32*vec.y+v33*vec.z+v34*vec.w;
        result.w = v41*vec.x+v42*vec.y+v43*vec.z+v44*vec.w;
        return result;
    }

    private static final FloatBuffer copyingBuffer = BufferUtils.createFloatBuffer(4*4);

    public static FiguraMat4 fromMatrix4f(Matrix4f mat) {
        copyingBuffer.clear();
        mat.writeColumnMajor(copyingBuffer);
        FiguraMat4 result = get();
        result.v11 = copyingBuffer.get();
        result.v21 = copyingBuffer.get();
        result.v31 = copyingBuffer.get();
        result.v41 = copyingBuffer.get();
        result.v12 = copyingBuffer.get();
        result.v22 = copyingBuffer.get();
        result.v32 = copyingBuffer.get();
        result.v42 = copyingBuffer.get();
        result.v13 = copyingBuffer.get();
        result.v23 = copyingBuffer.get();
        result.v33 = copyingBuffer.get();
        result.v43 = copyingBuffer.get();
        result.v14 = copyingBuffer.get();
        result.v24 = copyingBuffer.get();
        result.v34 = copyingBuffer.get();
        result.v44 = copyingBuffer.get();
        return result;
    }

    public Matrix4f toMatrix4f() {
        copyingBuffer.clear();
        copyingBuffer
                .put((float) v11).put((float) v21).put((float) v31).put((float) v41)
                .put((float) v12).put((float) v22).put((float) v32).put((float) v42)
                .put((float) v13).put((float) v23).put((float) v33).put((float) v43)
                .put((float) v14).put((float) v24).put((float) v34).put((float) v44);
        Matrix4f result = new Matrix4f();
        result.readColumnMajor(copyingBuffer);
        return result;
    }

    //Lua interaction

    public static FiguraMat4 __mul(FiguraMat4 mat1, FiguraMat4 mat2) {
        return mat2.times(mat1);
    }

    public static FiguraVec4 __mul(FiguraMat4 mat, FiguraVec4 vec) {
        return mat.times(vec);
    }

}
