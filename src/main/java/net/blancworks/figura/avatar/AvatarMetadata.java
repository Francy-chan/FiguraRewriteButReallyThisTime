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

    public AvatarMetadata(String avatarName, String cardColor) {
        this(avatarName,
                MinecraftClient.getInstance().getSession().getUsername(),
                UUID.randomUUID(),
                MinecraftClient.getInstance().getSession().getProfile().getId(),
                cardColor
        );
    }

    public AvatarMetadata(String avatarName, String creatorName, UUID avatarUUID, UUID creatorUUID, String cardColor) {
        this.avatarName = avatarName;
        this.creatorName = creatorName;
        this.avatarUUID = avatarUUID;
        this.creatorUUID = creatorUUID;
        this.cardColor = cardColor;
    }

    public void uploadAvatarMetadata() {
        //send some stuff to the backend or something
    }

}
