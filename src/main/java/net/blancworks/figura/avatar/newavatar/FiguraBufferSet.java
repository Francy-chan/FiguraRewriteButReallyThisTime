package net.blancworks.figura.avatar.newavatar;

import net.blancworks.figura.avatar.rendering.TransformData;
import net.blancworks.figura.math.matrix.FiguraMat3;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.utils.CacheStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

/**
 * Contains all the buffers for a figura avatar.
 * Also takes on the role of the FiguraRenderingState from before.
 */
public class FiguraBufferSet {

    private final FiguraBuffer[] buffers;
    private final CacheStack<FiguraMat4, TransformData> posMatrices = new Mat4Stack();
    private final CacheStack<FiguraMat3, TransformData> normalMatrices = new Mat3Stack();
    private int currentLight;
    private int currentOverlay;

    public FiguraBufferSet(List<FiguraBuffer> buffers) {
        this.buffers = buffers.toArray(new FiguraBuffer[0]);
    }

    public void draw(VertexConsumerProvider vcp) {
        for (FiguraBuffer buffer : buffers) {
            buffer.submitToVertexConsumers(vcp, currentLight, currentOverlay);
            buffer.clear();
        }
    }

    public void setLight(int light) {
        currentLight = light;
    }
    public void setOverlay(int overlay) {
        currentOverlay = overlay;
    }

    public void pushTransform(TransformData transformData) {
        posMatrices.push(transformData);
        normalMatrices.push(transformData);
        updateMatrices();
    }

    public void popTransform() {
        posMatrices.pop();
        normalMatrices.pop();
        updateMatrices();
    }

    private void updateMatrices() {
        FiguraMat4 mat4 = posMatrices.peek();
        FiguraMat3 mat3 = normalMatrices.peek();
        for (FiguraBuffer buffer : buffers)
            buffer.setMatrices(mat4, mat3);
    }

    public void pushVertices(int bufferIndex, int count) {
        for (int i = count; i > 0; i--)
            buffers[bufferIndex].pushVertex();
    }

    public void resetAndCopyFromStack(MatrixStack matrices) {
        posMatrices.fullClear();
        normalMatrices.fullClear();
        TransformData transformData = new TransformData();
        transformData.positionMatrix.copyFrom(FiguraMat4.fromMatrix4f(matrices.peek().getPositionMatrix()).free());
        transformData.normalMatrix.copyFrom(FiguraMat3.fromMatrix3f(matrices.peek().getNormalMatrix()).free());
        transformData.needsMatrixRecalculation = false;
        pushTransform(transformData);
    }

    private boolean texturesUploaded = false;
    public void uploadTexturesIfNeeded() {
        if (texturesUploaded) return;
        for (FiguraBuffer buffer : buffers)
            buffer.uploadTexturesIfNeeded();
        texturesUploaded = true;
    }

    public void close() {
        for (FiguraBuffer buffer : buffers)
            buffer.close();
    }

    private static class Mat4Stack extends CacheStack<FiguraMat4, TransformData> {
        @Override
        protected FiguraMat4 getNew() {
            return FiguraMat4.get();
        }
        @Override
        protected void modify(FiguraMat4 valueToModify, TransformData modifierArg) {
            valueToModify.copyFrom(modifierArg.positionMatrix.times(valueToModify).free());
            //valueToModify.multiply(modifierArg.positionMatrix);
        }
        @Override
        protected void copy(FiguraMat4 from, FiguraMat4 to) {
            to.copyFrom(from);
        }
        @Override
        protected void release(FiguraMat4 item) {
            item.free();
        }
    }
    private static class Mat3Stack extends CacheStack<FiguraMat3, TransformData> {
        @Override
        protected FiguraMat3 getNew() {
            return FiguraMat3.get();
        }
        @Override
        protected void modify(FiguraMat3 valueToModify, TransformData modifierArg) {
            valueToModify.copyFrom(modifierArg.normalMatrix.times(valueToModify).free());
            //valueToModify.multiply(modifierArg.normalMatrix);
        }
        @Override
        protected void copy(FiguraMat3 from, FiguraMat3 to) {
            to.copyFrom(from);
        }
        @Override
        protected void release(FiguraMat3 item) {
            item.free();
        }
    }
}
