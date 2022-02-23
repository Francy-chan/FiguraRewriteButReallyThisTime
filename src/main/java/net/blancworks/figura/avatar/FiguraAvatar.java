package net.blancworks.figura.avatar;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.components.model.FiguraModelsContainer;
import net.blancworks.figura.avatar.components.script.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.components.texture.FiguraTextureGroupManager;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.io.Closeable;
import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;

/**
 * The most top-level part of figura avatars: The Avatar class.
 * <p>
 * Essentially just acts as a holder for avatar components, and something to relay events to those components.
 */
public class FiguraAvatar {
    // -- Variables -- //
    //This cleaner manages the cleanup of native assets used by FiguraAvatars.
    private static final Cleaner avatarCleaner = Cleaner.create();

    //List of assets to be cleaned up when avatar is GC'd.
    public final List<Closeable> closeableAssets;

    //Components of the avatar
    public final FiguraModelsContainer models = new FiguraModelsContainer();
    public final FiguraScriptEnvironment scriptEnv = new FiguraScriptEnvironment();
    public final FiguraTextureGroupManager textureGroupManager = new FiguraTextureGroupManager();


    // -- Constructors -- //
    private FiguraAvatar(List<Closeable> assetList) {
        this.closeableAssets = assetList;
    }

    // -- Functions -- //

    public static FiguraAvatar getAvatar() {
        List<Closeable> assetList = new ArrayList<>();
        FiguraAvatar newAvatar = new FiguraAvatar(assetList);

        //Register a cleanup task to run once avatar is GC'd.
        avatarCleaner.register(newAvatar, new AvatarCleanupTask(assetList));

        return newAvatar;
    }

    public <T extends Entity> void tick(T entity) {
        //Call tick on the script
        scriptEnv.tick(this);
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
        scriptEnv.render(this, tickDelta);

        // Render the model (submit vertices)
        models.render(state);

        // Draw the vertices created by the model.
        state.draw();
    }



    // -- Nested classes -- //
    private record AvatarCleanupTask(List<Closeable> assetList) implements Runnable{
        @Override
        public void run() {
            for (Closeable closeable : assetList) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    FiguraMod.LOGGER.error(e);
                }
            }

            assetList.clear();
        }
    }
}
