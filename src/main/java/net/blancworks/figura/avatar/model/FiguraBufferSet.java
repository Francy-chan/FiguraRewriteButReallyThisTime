package net.blancworks.figura.avatar.model;

import net.blancworks.figura.math.matrix.FiguraMat3;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.utils.CacheStack;
import net.blancworks.figura.utils.TransformData;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;
import java.util.Stack;

/**
 * Contains all the buffers for a figura avatar.
 * Also takes on the role of the FiguraRenderingState from before.
 */
public class FiguraBufferSet {

    private int remainingVerts; //Complexity limiting
    private final FiguraBuffer[] buffers;
    private final CacheStack<FiguraMat4, TransformData> posMatrices = new Mat4Stack();
    private final CacheStack<FiguraMat3, TransformData> normalMatrices = new Mat3Stack();
    private final Stack<String> renderModes = new Stack<>();
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

    public void pushModifications(TransformData transformData, String renderMode) {
        posMatrices.push(transformData);
        normalMatrices.push(transformData);

        if((renderMode == null || renderMode.length() == 0) && renderModes.size() > 0)
            renderModes.push(renderModes.peek());
        else
            renderModes.push(renderMode);
        updateModifications();
    }

    public void popTransform() {
        posMatrices.pop();
        normalMatrices.pop();
        renderModes.pop();
        updateModifications();
    }

    private void updateModifications() {
        FiguraMat4 mat4 = posMatrices.peek();
        FiguraMat3 mat3 = normalMatrices.peek();
        String renderMode = renderModes.peek();
        for (FiguraBuffer buffer : buffers)
            buffer.setModifications(mat4, mat3, renderMode);
    }

    public void setRemaining(int maxVerts) {
        remainingVerts = maxVerts;
    }

    //Returns true when all vertices were pushed successfully
    public boolean pushVertices(int bufferIndex, int count) {
        while (count-- > 0 && remainingVerts-- > 0)
            buffers[bufferIndex].pushVertex();
        return count == -1;
    }

    public void resetAndCopyFromStack(MatrixStack matrices) {
        posMatrices.fullClear();
        normalMatrices.fullClear();
        renderModes.clear();
        TransformData transformData = new TransformData();
        transformData.positionMatrix.copyFrom(FiguraMat4.fromMatrix4f(matrices.peek().getPositionMatrix()).free());
        transformData.normalMatrix.copyFrom(FiguraMat3.fromMatrix3f(matrices.peek().getNormalMatrix()).free());
        transformData.needsMatrixRecalculation = false;
        pushModifications(transformData, null);
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
