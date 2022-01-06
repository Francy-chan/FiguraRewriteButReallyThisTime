package net.blancworks.figura.dealer;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.entity.Entity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Deals avatars from the non-persistent storage.
 * <p>
 * Ignores all UUIDs except the local player.
 */
public class FiguraLocalDealer extends FiguraDealer {

    @Override
    public CompletableFuture<FiguraAvatar> getAvatar(Entity e) {
        return CompletableFuture.completedFuture(new FiguraAvatar());
    }
}
