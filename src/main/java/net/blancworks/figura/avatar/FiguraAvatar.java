package net.blancworks.figura.avatar;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.components.model.FiguraModelsContainer;
import net.blancworks.figura.avatar.components.script.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.components.texture.FiguraTextureGroupManager;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * The most top-level part of figura avatars: The Avatar class.
 * <p>
 * Essentially just acts as a holder for avatar components, and something to relay events to those components.
 */
public class FiguraAvatar {
    // -- Variables -- //
    //Components of the avatar
    public final FiguraModelsContainer models = new FiguraModelsContainer(this);
    public final FiguraScriptEnvironment scriptEnv = new FiguraScriptEnvironment(this);
    public final FiguraTextureGroupManager textureGroupManager = new FiguraTextureGroupManager(this);

    // -- Functions -- //

    public <T extends Entity> void tick(T entity) {
        //Call tick on the script
        if (scriptEnv != null) scriptEnv.tick();
    }

    public <T extends Entity> void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        //Generate rendering state
        FiguraRenderingState<T> state = new FiguraRenderingState<T>();
        //Function args
        state.entity = entity;
        state.yaw = yaw;
        state.tickDelta = tickDelta;
        state.poseStack = matrices;
        state.vertexConsumerProvider = vertexConsumers;
        state.light = light;

        //Providers
        state.avatar = this;
        state.textureGroupManager = textureGroupManager;

        //Call script render event.
        scriptEnv.render(tickDelta);

        // Render the model (submit vertices)
        models.render(state);

        // Draw the vertices created by the model.
        state.draw();
    }

    //Cleans up all native objects.
    public void destroy() {
        FiguraMod.LOGGER.info("Destroying avatar");
        models.destroy();
        scriptEnv.destroy();
        textureGroupManager.destroy();
    }
}
