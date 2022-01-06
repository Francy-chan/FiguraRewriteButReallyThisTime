package net.blancworks.figura.entity;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.dealer.FiguraHouse;

import java.util.concurrent.CompletableFuture;

/**
 * This class stores the Figura state for a related entity.
 */
public class FiguraEntityMetadata {

    public final CompletableFuture<FiguraAvatar>[] avatarsByID;

    public FiguraEntityMetadata() {
        avatarsByID = new CompletableFuture[FiguraHouse.dealers.length];
    }
}
