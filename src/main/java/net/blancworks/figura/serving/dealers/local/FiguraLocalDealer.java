package net.blancworks.figura.serving.dealers.local;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.importing.AvatarFileSet;
import net.blancworks.figura.avatar.importing.ImporterManager;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

/**
 * Deals avatars from the non-persistent storage.
 * <p>
 * Ignores all UUIDs except the local player.
 */
public class FiguraLocalDealer extends FiguraDealer {
    // -- Variables -- //
    public static final Identifier ID = new Identifier("figura", "local");
    public static final AvatarGroup localPlayerAvatarGroup = new AvatarGroup();

    // -- Functions -- //

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public <T extends Entity> AvatarGroup getGroup(T entity) {

        //Read file from local folder for now
        if (entity == MinecraftClient.getInstance().player) {
            AvatarFileSet afs = ImporterManager.foundAvatars.get(Path.of("test"));

            if (afs != null) {
                return localPlayerAvatarGroup;
            }
        }
        return null;
    }

    //Unused in local dealer
    @Override
    protected <T extends Entity> AvatarGroup requestForEntity(T entity) {
        return new AvatarGroup();
    }
}
