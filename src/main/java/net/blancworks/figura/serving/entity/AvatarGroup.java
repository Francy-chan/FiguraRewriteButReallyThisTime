package net.blancworks.figura.serving.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * Used by FiguraDealers to store and access multiple avatars at once.
 */
public class AvatarGroup {

    // -- Variables -- //
    public static final int MAX_AVATARS = 4;
    public final FiguraAvatar[] avatars = new FiguraAvatar[MAX_AVATARS];


    // -- Functions -- //

    public <T extends Entity> void tick(T entity) {
        for (FiguraAvatar avatar : avatars) {
            if (avatar == null) continue;

            avatar.tick(entity);
        }
    }

    public <T extends Entity> void render(T targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        for (FiguraAvatar avatar : avatars) {
            if (avatar == null) continue;

            avatar.render(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }
}
