package net.blancworks.figura.dealer;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.importing.ImporterManager;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

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
        if (e == MinecraftClient.getInstance().player) {

            //Import files from local directory into NBT compound.
            NbtCompound avatarCompound = new NbtCompound();
            ImporterManager.importDirectory(FiguraMod.getLocalAvatarDirectory().resolve("test").toAbsolutePath(), avatarCompound);

            FiguraAvatar localAvatar = new FiguraAvatar();
            FiguraAvatarNbtConverter.readNBT(localAvatar, avatarCompound);

            return CompletableFuture.completedFuture(localAvatar);
        } else {
            return CompletableFuture.completedFuture(new FiguraAvatar());
        }
    }
}
