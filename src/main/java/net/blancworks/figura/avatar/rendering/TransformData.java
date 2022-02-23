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

    public void recalculateMatrix() {
        if (needsMatrixRecalculation) {
            positionMatrix.resetToIdentity();
            positionMatrix.translate(
                    -origin.x,
                    -origin.y,
                    -origin.z
            );
            positionMatrix.scale(
                    scale.x,
                    scale.y,
                    scale.z
            );
            positionMatrix.translate(
                    position.x,
                    position.y,
                    position.z
            );
            positionMatrix.rotateZYX(
                    rotation.x,
                    rotation.y,
                    rotation.z
            );
            positionMatrix.translate(
                    origin.x,
                    origin.y,
                    origin.z
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
