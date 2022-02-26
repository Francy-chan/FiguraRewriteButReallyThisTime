package net.blancworks.figura.avatar;

import net.blancworks.figura.avatar.model.FiguraBufferSet;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.blancworks.figura.utils.TransformData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

import java.lang.ref.Cleaner;

public class FiguraAvatar {

    private static final Cleaner cleaner = Cleaner.create();

    private final FiguraBufferSet buffers;
    private final FiguraModelPart models;
    private final FiguraScriptEnvironment script;

    public FiguraAvatar(FiguraBufferSet buffers, FiguraModelPart models, FiguraScriptEnvironment script) {

        this.buffers = buffers;
        this.models = models;
        this.script = script;

        cleaner.register(this, new AvatarCleanTask(buffers, script));
    }

    public void tick(Entity e) {
        script.tick(this);
    }

    public FiguraScriptEnvironment getScript() {
        return script;
    }

    /**
     * Renders this avatar using compatibility mode. (currently the only mode)
     */

    private static final TransformData currentEntityTransform = new TransformData();

    public void renderImmediate(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        script.render(this, tickDelta);

        buffers.uploadTexturesIfNeeded();

        buffers.resetAndCopyFromStack(matrices);

        //updateEntityTransform(entity, yaw, tickDelta);
        //buffers.pushTransform(currentEntityTransform);

        models.renderImmediate(buffers);

        buffers.setLight(light);
        buffers.setOverlay(OverlayTexture.DEFAULT_UV);
        buffers.draw(vertexConsumers);
    }

    private static void updateEntityTransform(Entity entity, float yaw, float tickDelta) {
        if (entity instanceof LivingEntity e) //Super jank, temporary workaround for body yaw
            yaw = MathHelper.lerp(tickDelta, e.prevBodyYaw, e.bodyYaw);
        currentEntityTransform.rotation.set(0, 0, 0);
        currentEntityTransform.needsMatrixRecalculation = true;
        currentEntityTransform.recalculateMatrix();
    }

    public FiguraModelPart getRoot() {
        return models;
    }

    private static record AvatarCleanTask(FiguraBufferSet buffers, FiguraScriptEnvironment scriptEnv) implements Runnable {
        @Override
        public void run() {
            if (buffers != null)
                buffers.close();
            if (scriptEnv != null)
                scriptEnv.luaState.close();
        }
    }

}
