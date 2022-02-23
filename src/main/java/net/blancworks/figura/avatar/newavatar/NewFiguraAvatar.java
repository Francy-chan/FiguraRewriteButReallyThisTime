package net.blancworks.figura.avatar.newavatar;

import net.blancworks.figura.avatar.components.script.FiguraScriptEnvironment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.lang.ref.Cleaner;

public class NewFiguraAvatar {

    private static final Cleaner cleaner = Cleaner.create();

    private final FiguraBufferSet buffers;
    private final NewFiguraModelPart models;
    private final FiguraScriptEnvironment script;

    public NewFiguraAvatar(FiguraBufferSet buffers, NewFiguraModelPart models, FiguraScriptEnvironment script) {

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
    public void renderImmediate(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        script.render(this, tickDelta);

        buffers.uploadTexturesIfNeeded();

        buffers.resetAndCopyFromStack(matrices);
        models.renderImmediate(buffers);

        buffers.setLight(light);
        buffers.setOverlay(OverlayTexture.DEFAULT_UV);
        buffers.draw(vertexConsumers);
    }

    public NewFiguraModelPart getRoot() {
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
