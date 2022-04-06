package net.blancworks.figura.avatar;

/**
 * Super bare-bones class since I don't know what's going in here really
 */
public record AvatarMetadata(String avatarName, String creatorName, String cardColor, String background) {

    public void uploadAvatarMetadata() {
        //send some stuff to the backend or something
    }
}
