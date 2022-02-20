package net.blancworks.figura.math.matrix;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.util.math.Matrix3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

public class FiguraMat3 extends ObjectWrapper<FiguraMat3> {

    @LuaWhitelist
    public double v11 = 1, v12, v13, v21, v22 = 1, v23, v31, v32, v33 = 1;

    private static Queue<FiguraMat3> pool = new LinkedList<>();

    private FiguraMat3() {}

    public static FiguraMat3 get() {
        FiguraMat3 result = pool.poll();
        if (result == null)
            result = new FiguraMat3();
        else
            result.resetToIdentity();
        return result;
    }

    @LuaWhitelist
    public void resetToIdentity() {
        v11 = v22 = v33 = 1;
        v12 = v13 = v21 = v23 = v31 = v32 = 0;
    }

    @LuaWhitelist
    public FiguraMat3 free() {
        pool.add(this);
        return this;
    }

    //Static matrix creator methods

    public static FiguraMat3 createScaleMatrix(double x, double y, double z) {
        FiguraMat3 result = get();
        result.v11 = x;
        result.v22 = y;
        result.v33 = z;
        return result;
    }

    public static FiguraMat3 createXRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat3 result = get();
        result.v22 = result.v33 = c;
        result.v23 = -s;
        result.v32 = s;
        return result;
    }

    public static FiguraMat3 createYRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat3 result = get();
        result.v11 = result.v33 = c;
        result.v13 = s;
        result.v31 = -s;
        return result;
    }

    public static FiguraMat3 createZRotationMatrix(double degrees) {
        degrees = Math.toRadians(degrees);
        double s = Math.sin(degrees);
        double c = Math.cos(degrees);
        FiguraMat3 result = get();
        result.v11 = result.v22 = c;
        result.v12 = -s;
        result.v21 = s;
        return result;
    }

    public static FiguraMat3 createZYXRotationMatrix(double x, double y, double z) {
        x = Math.toRadians(x);
        y = Math.toRadians(y);
        z = Math.toRadians(z);

        double a = Math.cos(x);
        double b = Math.sin(x);
        double c = Math.cos(y);
        double d = Math.sin(y);
        double e = Math.cos(z);
        double f = Math.sin(z);

        FiguraMat3 result = get();
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

    //Mutation methods

    public void scale(double x, double y, double z) {
        v11 *= x;
        v12 *= x;
        v13 *= x;
        v21 *= y;
        v22 *= y;
        v23 *= y;
        v31 *= z;
        v32 *= z;
        v33 *= z;
    }

    public void rotateX(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv21 = c*v21 - s*v31;
        double nv22 = c*v22 - s*v32;
        double nv23 = c*v23 - s*v33;

        v31 = s*v21 + c*v31;
        v32 = s*v22 + c*v32;
        v33 = s*v23 + c*v33;

        v21 = nv21;
        v22 = nv22;
        v23 = nv23;
    }

    public void rotateY(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv11 = c*v11 + s*v31;
        double nv12 = c*v12 + s*v32;
        double nv13 = c*v13 + s*v33;

        v31 = c*v31 - s*v11;
        v32 = c*v32 - s*v12;
        v33 = c*v33 - s*v13;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
    }

    public void rotateZ(double degrees) {
        degrees = Math.toRadians(degrees);
        double c = Math.cos(degrees);
        double s = Math.sin(degrees);

        double nv11 = c*v11 - s*v21;
        double nv12 = c*v12 - s*v22;
        double nv13 = c*v13 - s*v23;

        v21 = c*v21 + s*v11;
        v22 = c*v22 + s*v12;
        v23 = c*v23 + s*v13;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
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
        v33 = -d*v13 + bc*v23 + ac*v33;

        v11 = nv11;
        v21 = nv21;
        v31 = nv31;
        v12 = nv12;
        v22 = nv22;
        v32 = nv32;
        v13 = nv13;
        v23 = nv23;
    }

    public void multiply(FiguraMat3 o) {
        double nv11 = o.v11*v11+o.v12*v21+o.v13*v31;
        double nv12 = o.v11*v12+o.v12*v22+o.v13*v32;
        double nv13 = o.v11*v13+o.v12*v23+o.v13*v33;

        double nv21 = o.v21*v11+o.v22*v21+o.v23*v31;
        double nv22 = o.v21*v12+o.v22*v22+o.v23*v32;
        double nv23 = o.v21*v13+o.v22*v23+o.v23*v33;

        double nv31 = o.v31*v11+o.v32*v21+o.v33*v31;
        double nv32 = o.v31*v12+o.v32*v22+o.v33*v32;
        v33 = o.v31*v13+o.v32*v23+o.v33*v33;

        v11 = nv11;
        v12 = nv12;
        v13 = nv13;
        v21 = nv21;
        v22 = nv22;
        v23 = nv23;
        v31 = nv31;
        v32 = nv32;
    }

    @LuaWhitelist
    public void copyFrom(FiguraMat3 other) {
        v11 = other.v11;
        v12 = other.v12;
        v13 = other.v13;
        v21 = other.v21;
        v22 = other.v22;
        v23 = other.v23;
        v31 = other.v31;
        v32 = other.v32;
        v33 = other.v33;
    }

    @LuaWhitelist
    public FiguraMat3 transpose() {
        FiguraMat3 result = get();
        result.v11 = v11;
        result.v12 = v21;
        result.v13 = v31;
        result.v21 = v12;
        result.v22 = v22;
        result.v23 = v32;
        result.v31 = v13;
        result.v32 = v23;
        result.v33 = v33;
        return result;
    }

    //Returns the product of the matrices, with "o" on the left.
    public FiguraMat3 times(FiguraMat3 o) {
        FiguraMat3 result = get();

        result.v11 = o.v11*v11+o.v12*v21+o.v13*v31;
        result.v12 = o.v11*v12+o.v12*v22+o.v13*v32;
        result.v13 = o.v11*v13+o.v12*v23+o.v13*v33;

        result.v21 = o.v21*v11+o.v22*v21+o.v23*v31;
        result.v22 = o.v21*v12+o.v22*v22+o.v23*v32;
        result.v23 = o.v21*v13+o.v22*v23+o.v23*v33;

        result.v31 = o.v31*v11+o.v32*v21+o.v33*v31;
        result.v32 = o.v31*v12+o.v32*v22+o.v33*v32;
        result.v33 = o.v31*v13+o.v32*v23+o.v33*v33;

        return result;
    }

    public FiguraVec3 times(FiguraVec3 vec) {
        FiguraVec3 result = FiguraVec3.get();
        result.x = v11*vec.x+v12*vec.y+v13*vec.z;
        result.y = v21*vec.x+v22*vec.y+v23*vec.z;
        result.z = v31*vec.x+v32*vec.y+v33*vec.z;
        return result;
    }

    private static final FloatBuffer copyingBuffer = BufferUtils.createFloatBuffer(3*3);

    public static FiguraMat3 fromMatrix3f(Matrix3f mat) {
        copyingBuffer.clear();
        mat.writeColumnMajor(copyingBuffer);
        FiguraMat3 result = get();
        result.v11 = copyingBuffer.get();
        result.v21 = copyingBuffer.get();
        result.v31 = copyingBuffer.get();
        result.v12 = copyingBuffer.get();
        result.v22 = copyingBuffer.get();
        result.v32 = copyingBuffer.get();
        result.v13 = copyingBuffer.get();
        result.v23 = copyingBuffer.get();
        result.v33 = copyingBuffer.get();
        return result;
    }

    public Matrix3f toMatrix3f() {
        copyingBuffer.clear();
        copyingBuffer
                .put((float) v11).put((float) v21).put((float) v31)
                .put((float) v12).put((float) v22).put((float) v32)
                .put((float) v13).put((float) v23).put((float) v33);
        Matrix3f result = new Matrix3f();
        result.readColumnMajor(copyingBuffer);
        return result;
    }

    //Lua interaction

    public static FiguraMat3 __mul(FiguraMat3 mat1, FiguraMat3 mat2) {
        return mat2.times(mat1);
    }

    public static FiguraVec3 __mul(FiguraMat3 mat, FiguraVec3 vec) {
        return mat.times(vec);
    }

}
