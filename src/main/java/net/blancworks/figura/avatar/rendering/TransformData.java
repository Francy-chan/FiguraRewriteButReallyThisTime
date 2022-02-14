package net.blancworks.figura.avatar.rendering;

import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix3;
import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix4;
import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec3;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3f;

public class TransformData {

    public final LuaMatrix4 positionMatrix = LuaMatrix4.get();
    public final LuaMatrix3 normalMatrix = LuaMatrix3.get();

    public boolean needsMatrixRecalculation = true;

    public final LuaVec3 position = LuaVec3.get();
    public final LuaVec3 origin = LuaVec3.get();
    public final LuaVec3 rotation = LuaVec3.get();
    public final LuaVec3 scale = LuaVec3.get(1, 1, 1);

    public void readFromNBT(NbtCompound tag) {
        NbtList originList = tag.getList("origin", NbtElement.FLOAT_TYPE);
        NbtList rotList = tag.getList("rotation", NbtElement.FLOAT_TYPE);

        origin.x = originList.getFloat(0);
        origin.y = originList.getFloat(1);
        origin.z = originList.getFloat(2);

        rotation.x = rotList.getFloat(0);
        rotation.y = rotList.getFloat(1);
        rotation.z = rotList.getFloat(2);
    }

    private void recalculateMatrix() {
        if (needsMatrixRecalculation) {
            positionMatrix.resetToIdentity();
            positionMatrix.translate(
                    origin.x / -16,
                    origin.y / -16,
                    origin.z / -16
            );
            positionMatrix.scale(
                    scale.x,
                    scale.y,
                    scale.z
            );
            positionMatrix.translate(
                    position.x / 16,
                    position.y / 16,
                    position.z / 16
            );
            positionMatrix.rotateZYX(
                    rotation.x,
                    rotation.y,
                    rotation.z
            );
            positionMatrix.translate(
                    origin.x / 16,
                    origin.y / 16,
                    origin.z / 16
            );

            //Normals
            normalMatrix.resetToIdentity();
            double c = Math.cbrt(scale.x * scale.y * scale.z); //Maybe later use fast inverse cube root here like minecraft does?
            normalMatrix.scale(
                    c == 0 && scale.x == 0 ? 1 : c / scale.x,
                    c == 0 && scale.y == 0 ? 1 : c / scale.y,
                    c == 0 && scale.z == 0 ? 1 : c / scale.z
            );
            normalMatrix.rotateZYX(
                    rotation.x,
                    rotation.y,
                    rotation.z
            );
        }
        needsMatrixRecalculation = false;
    }

    public void applyToStack(MatrixStack matrices) {
        recalculateMatrix();
        matrices.multiplyPositionMatrix(positionMatrix.toMatrix4f());
        matrices.peek().getNormalMatrix().multiply(normalMatrix.toMatrix3f());
    }
}
