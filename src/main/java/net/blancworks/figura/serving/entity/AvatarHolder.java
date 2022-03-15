package net.blancworks.figura.serving.entity;

import net.blancworks.figura.avatar.FiguraAvatar;

/**
 * This is basically a fancy wrapper around a FiguraAvatar array, used to reference the avatars for an entity.
 *
 * Name comes from the fact that these objects "hold on" to the avatar array, to make sure it doesn't go away.
 */
public class AvatarHolder extends FiguraEventReceiverArray<FiguraAvatar> {
    // -- Constructors -- //

    public boolean isHost = false;

    public AvatarHolder(FiguraAvatar[] avatars){
        this.entries = avatars;
    }
}
