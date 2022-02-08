package net.blancworks.figura.serving.entity;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.util.concurrent.CompletableFuture;

/**
 * Used by FiguraDealers to store and access multiple avatars at once.
 */
public class AvatarGroup {

    // -- Variables -- //
    public final CompletableFuture<FiguraAvatar>[] avatars = new CompletableFuture[4];


    // -- Functions -- //

    public <T extends Entity> void tick(T entity) {
        for (CompletableFuture<FiguraAvatar> avatarFuture : avatars) {
            if (avatarFuture == null) continue;
            ;

            try {
                var avatar = avatarFuture.getNow(null);
                if (avatar == null) continue;

                avatar.tick(entity);
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
        }
    }

    public <T extends Entity> void render(T targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        for (CompletableFuture<FiguraAvatar> avatarFuture : avatars) {
            if (avatarFuture == null) continue;
            ;

            try {
                var avatar = avatarFuture.getNow(null);
                if (avatar == null) continue;

                avatar.render(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
        }
    }
}
