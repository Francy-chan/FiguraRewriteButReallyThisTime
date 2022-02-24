package net.blancworks.figura.serving.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

/**
 * This is basically a fancy wrapper around a FiguraAvatar array, used to reference the avatars for an entity.
 *
 * Name comes from the fact that these objects "hold on" to the avatar array, to make sure it doesn't go away.
 */
public class AvatarHolder {

    // -- Variables -- //
    public final FiguraAvatar[] avatars;


    // -- Constructors -- //

    public AvatarHolder(FiguraAvatar[] avatars){
        this.avatars = avatars;
    }

    // -- Functions -- //

    public <T extends Entity> void tick(T entity) {
        if(avatars == null) return;

        for (FiguraAvatar avatar : avatars) {
            if (avatar == null) continue;

            avatar.tick(entity);
        }
    }

    public <T extends Entity> void render(T targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if(avatars == null) return;

        for (FiguraAvatar avatar : avatars) {
            if (avatar == null) continue;

            avatar.renderImmediate(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
        }
    }
}
