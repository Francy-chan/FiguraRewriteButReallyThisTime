package net.blancworks.figura.avatar.rendering;

import net.blancworks.figura.math.matrix.FiguraMat3;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class TransformData {

    public final FiguraMat4 positionMatrix = FiguraMat4.get();
    public final FiguraMat3 normalMatrix = FiguraMat3.get();

    public boolean needsMatrixRecalculation = true;

    public final FiguraVec3 position = FiguraVec3.get();
    public final FiguraVec3 origin = FiguraVec3.get();
    public final FiguraVec3 rotation = FiguraVec3.get();
    public final FiguraVec3 scale = FiguraVec3.get(1, 1, 1);

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
