package net.blancworks.figura.avatar;

import net.blancworks.figura.avatar.customizations.FiguraCustomizationManager;
import net.blancworks.figura.avatar.model.FiguraBufferSet;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.lang.ref.Cleaner;

public class FiguraAvatar {

    private static final Cleaner cleaner = Cleaner.create();

    private final FiguraBufferSet buffers;
    private final FiguraModelPart models;
    private final FiguraScriptEnvironment script;
    public TrustContainer trustContainer;

    public final FiguraCustomizationManager customizationManager;

    public FiguraAvatar(FiguraBufferSet buffers, FiguraModelPart models, FiguraScriptEnvironment script) {

        this.buffers = buffers;
        this.models = models;
        this.script = script;

        customizationManager = new FiguraCustomizationManager(this);

        cleaner.register(this, new AvatarCleanTask(buffers, script));
    }

    public FiguraModelPart getRoot() {
        return models;
    }

    public FiguraScriptEnvironment getScript() {
        return script;
    }

    public TrustContainer getTrustContainer(Entity target) {
        if (target instanceof PlayerEntity pEnt)
            if (trustContainer == null)
                trustContainer = TrustManager.get(target.getUuid());

        return trustContainer;
    }

    public void tick(Entity e) {
        getTrustContainer(e);
        script.isHost = (e == MinecraftClient.getInstance().player);
        script.tick(this);
    }


    /**
     * Renders this avatar using compatibility mode. (currently the only mode)
     */
    public void renderImmediate(Entity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        getTrustContainer(entity);
        script.isHost = (entity == MinecraftClient.getInstance().player);
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
