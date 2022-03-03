package net.blancworks.figura.avatar.model;

import net.blancworks.figura.utils.math.matrix.FiguraMat3;
import net.blancworks.figura.utils.math.matrix.FiguraMat4;
import net.blancworks.figura.utils.CacheStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

/**
 * Contains all the buffers for a figura avatar.
 * Also takes on the role of the FiguraRenderingState from before.
 */
public class FiguraBufferSet {

    private int remainingVerts; //Complexity limiting
    private final FiguraBuffer[] buffers;
    private final CacheStack<PartCustomization, PartCustomization> customizationStack = new CustomizationStack();

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

    public void pushCustomization(PartCustomization customization) {
        customizationStack.push(customization);
        updateCustomizations();
    }

    public void popCustomization() {
        customizationStack.pop();
        updateCustomizations();
    }

    private void updateCustomizations() {
        PartCustomization topCustomization = customizationStack.peek();
        for (FiguraBuffer buffer : buffers)
            buffer.setCustomization(topCustomization);
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
        customizationStack.fullClear();

        PartCustomization customization = new PartCustomization();
        customization.transformData.positionMatrix.copyFrom(FiguraMat4.fromMatrix4f(matrices.peek().getPositionMatrix()).free());
        customization.transformData.normalMatrix.copyFrom(FiguraMat3.fromMatrix3f(matrices.peek().getNormalMatrix()).free());
        customization.transformData.needsMatrixRecalculation = false;

        pushCustomization(customization);
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

    private static class CustomizationStack extends CacheStack<PartCustomization, PartCustomization> {
        @Override
        protected PartCustomization getNew() {
            return new PartCustomization();
        }
        @Override
        protected void modify(PartCustomization valueToModify, PartCustomization modifierArg) {
            valueToModify.transformData.positionMatrix.rightMultiply(modifierArg.transformData.positionMatrix);
            valueToModify.transformData.normalMatrix.rightMultiply(modifierArg.transformData.normalMatrix);

            valueToModify.color.multiply(modifierArg.color);

            if (modifierArg.renderMode != null && !modifierArg.renderMode.equals(""))
                valueToModify.renderMode = modifierArg.renderMode;
        }
        @Override
        protected void copy(PartCustomization from, PartCustomization to) {
            to.transformData.copyFrom(from.transformData);
            to.color.copyFrom(from.color);
            to.renderMode = from.renderMode;
        }
        @Override
        protected void release(PartCustomization item) {
        }
    }
}
