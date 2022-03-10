package net.blancworks.figura.serving.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

import java.util.UUID;

/**
 * This is basically a fancy wrapper around a FiguraAvatar array, used to reference the avatars for an entity.
 *
 * Name comes from the fact that these objects "hold on" to the avatar array, to make sure it doesn't go away.
 */
public class AvatarHolder extends FiguraEntityReceiverArray<FiguraAvatar> {
    // -- Constructors -- //

    public AvatarHolder(FiguraAvatar[] avatars){
        this.entries = avatars;
    }
}
