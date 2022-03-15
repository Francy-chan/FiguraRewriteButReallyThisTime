package net.blancworks.figura.avatar;

import net.blancworks.figura.avatar.customizations.FiguraCustomizationManager;
import net.blancworks.figura.avatar.model.FiguraBufferSet;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.pings.Ping;
import net.blancworks.figura.avatar.pings.PingManager;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.blancworks.figura.serving.entity.FiguraEventReceiver;
import net.blancworks.figura.trust.TrustContainer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.lang.ref.Cleaner;
import java.util.Queue;

public class FiguraAvatar implements FiguraEventReceiver {

    private static final Cleaner cleaner = Cleaner.create();

    public int slot = -1; // This is the slot that the avatar has been loaded into, if any.

    private final FiguraBufferSet buffers;
    private final FiguraModelPart models;
    private final FiguraScriptEnvironment script;
    public TrustContainer trustContainer;

    public final FiguraCustomizationManager customizationManager;
    public final PingManager pingManager;

    public FiguraAvatar(FiguraBufferSet buffers, FiguraModelPart models, FiguraScriptEnvironment script) {

        this.buffers = buffers;
        this.models = models;
        this.script = script;

        customizationManager = new FiguraCustomizationManager(this);

        cleaner.register(this, new AvatarCleanTask(buffers, script));

        pingManager = new PingManager(this);
    }

    public FiguraModelPart getRoot() {
        return models;
    }

    public FiguraScriptEnvironment getScript() {
        return script;
    }

    @Override
    public void setTrustContainer(TrustContainer tc) {
        trustContainer = tc;
    }

    public synchronized void tick() {
        script.tick(this);
        pingManager.tick();
    }

    @Override
    public synchronized void render(Entity targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        renderImmediate(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }


    /**
     * Renders this avatar using compatibility mode. (currently the only mode)
     */
    public void renderImmediate(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        script.render(this, tickDelta);

        buffers.uploadTexturesIfNeeded();
        buffers.resetAndCopyFromStack(matrices);

        buffers.setRemaining(trustContainer == null ? Integer.MAX_VALUE : trustContainer.get(TrustContainer.Trust.COMPLEXITY));

        models.renderImmediate(buffers);

        buffers.setLight(light);
        buffers.setOverlay(OverlayTexture.DEFAULT_UV);
        buffers.draw(vertexConsumers);
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
