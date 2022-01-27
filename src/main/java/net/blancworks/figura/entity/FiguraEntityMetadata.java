package net.blancworks.figura.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.dealer.FiguraHouse;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.util.concurrent.CompletableFuture;

/**
 * This class stores the Figura state for a related entity.
 */
public class FiguraEntityMetadata {

    public final CompletableFuture<FiguraAvatar>[] avatarsByID;

    public FiguraEntityMetadata() {
        avatarsByID = new CompletableFuture[FiguraHouse.dealers.length];
    }

    /**
     * Called by an entity when they tick.
     * Ticks all avatars in the metadata.
     */
    public <T extends Entity> void onTick(T entity) {
        for (CompletableFuture<FiguraAvatar> future : avatarsByID) {
            if (future != null && future.isDone()) {
                try {
                    FiguraAvatar avatar = future.get();

                    //Render avatar
                    avatar.tick(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called by the entity when they render.
     * Renders the first avatar, but calls the render event in all avatars.
     */
    public <T extends Entity> void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        for (CompletableFuture<FiguraAvatar> future : avatarsByID) {
            if (future != null && future.isDone()) {
                try {
                    FiguraAvatar avatar = future.get();

                    //Render avatar
                    avatar.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
