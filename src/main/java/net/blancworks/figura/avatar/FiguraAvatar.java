package net.blancworks.figura.avatar;

import net.blancworks.figura.avatar.components.script.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.components.model.FiguraModel;
import net.blancworks.figura.avatar.components.texture.FiguraTextureGroupManager;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.lang.ref.Cleaner;
import java.util.ArrayList;

/**
 * The most top-level part of figura avatars: The Avatar class.
 * <p>
 * Essentially just acts as a holder for avatar components, and something to relay events to those components.
 */
public class FiguraAvatar {
    private static final Identifier figura_texid = new Identifier("textures/entity/creeper/creeper.png");
    private static final Cleaner avatarCleaner = Cleaner.create();

    private final ArrayList<FiguraNativeObject> nativeObjects = new ArrayList<>();

    //Components of the avatar
    public final FiguraModel model = new FiguraModel(this);
    public final FiguraScriptEnvironment scriptEnv = new FiguraScriptEnvironment(this);
    public final FiguraTextureGroupManager textureGroupManager = new FiguraTextureGroupManager(this);

    public FiguraAvatar(){
        avatarCleaner.register(this, new AvatarCleanTask(nativeObjects));
    }

    public <T extends Entity> void tick(T entity) {
        //Call tick on the script
        if(scriptEnv != null) scriptEnv.tick();
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
        model.render(state);

        // Draw the vertices created by the model.
        state.draw();
    }


    /**
     * Adds a native object to be cleaned up with this avatar.
     */
    public void trackNativeObject(FiguraNativeObject obj){
        nativeObjects.add(obj);
    }

    /**
     * Used by the avatar cleaner to clean up native assets.
     */
    private static class AvatarCleanTask implements Runnable {
        public final ArrayList<FiguraNativeObject> objects;

        public AvatarCleanTask(ArrayList<FiguraNativeObject> objectList){
            this.objects = objectList;
        }

        @Override
        public void run() {
            for (FiguraNativeObject object : objects) {
                object.destroy();
            }
        }
    }
}
