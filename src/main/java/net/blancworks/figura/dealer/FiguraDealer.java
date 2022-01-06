package net.blancworks.figura.dealer;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.entity.Entity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for creating and providing avatar cards from storage.
 * <p>
 * Provides, on request, a set of avatars for a given user's UUID asynchronously
 */
public abstract class FiguraDealer {
    //The ID of this dealer.
    public int id;

    public abstract CompletableFuture<FiguraAvatar> getAvatar(Entity entityID);
}
