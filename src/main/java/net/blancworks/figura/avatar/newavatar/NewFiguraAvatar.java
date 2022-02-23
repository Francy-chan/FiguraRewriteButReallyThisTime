package net.blancworks.figura.avatar.newavatar;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.lang.ref.Cleaner;

public class NewFiguraAvatar {

    private static final Cleaner cleaner = Cleaner.create();

    private final FiguraBufferSet buffers;
    private final NewFiguraModelPart models;

    public NewFiguraAvatar(FiguraBufferSet buffers, NewFiguraModelPart models) {

        this.buffers = buffers;
        this.models = models;

        cleaner.register(this, new AvatarCleanTask());
    }

    public void tick(Entity e) {

    }

    /**
     * Renders this avatar using compatibility mode. (currently the only mode)
     */
    public void renderImmediate(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        buffers.uploadTexturesIfNeeded();

        buffers.resetAndCopyFromStack(matrices);
        models.renderImmediate(buffers);

        buffers.setLight(light);
        buffers.setOverlay(OverlayTexture.DEFAULT_UV);
        buffers.draw(vertexConsumers);
    }


    private class AvatarCleanTask implements Runnable {
        @Override
        public void run() {
            buffers.close();
        }
    }

}
