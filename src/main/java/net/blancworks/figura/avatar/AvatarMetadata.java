package net.blancworks.figura.avatar;

import net.minecraft.client.MinecraftClient;

import java.util.UUID;

/**
 * Super bare-bones class since I don't know what's going in here really
 */
public class AvatarMetadata {

    public final String avatarName;
    public final String creatorName;
    private final UUID avatarUUID;
    private final UUID creatorUUID;
    public final String cardColor;
    public final String background;

    public AvatarMetadata(String avatarName, String cardColor, String background) {
        this(avatarName,
                MinecraftClient.getInstance().getSession().getUsername(),
                UUID.randomUUID(),
                MinecraftClient.getInstance().getSession().getProfile().getId(),
                cardColor,
                background
        );
    }

    public AvatarMetadata(String avatarName, String creatorName, UUID avatarUUID, UUID creatorUUID, String cardColor, String background) {
        this.avatarName = avatarName;
        this.creatorName = creatorName;
        this.avatarUUID = avatarUUID;
        this.creatorUUID = creatorUUID;
        this.cardColor = cardColor;
        this.background = background;
    }

    public void uploadAvatarMetadata() {
        //send some stuff to the backend or something
    }

}
