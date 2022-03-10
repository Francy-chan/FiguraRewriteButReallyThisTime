package net.blancworks.figura.serving.dealers.local;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * Deals avatars from the non-persistent storage.
 * <p>
 * Ignores all UUIDs except the local player.
 */
public class FiguraLocalDealer extends FiguraDealer {
    // -- Variables -- //
    public static final Identifier ID = new Identifier("figura", "local");
    public static final AvatarHolder localPlayerAvatarHolder = new AvatarHolder(new FiguraAvatar[MAX_AVATARS]);

    // -- Functions -- //

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public AvatarHolder getHolder(UUID id) {
        return MinecraftClient.getInstance().getSession().getProfile().getId().equals(id) ? localPlayerAvatarHolder : null;
    }
}
