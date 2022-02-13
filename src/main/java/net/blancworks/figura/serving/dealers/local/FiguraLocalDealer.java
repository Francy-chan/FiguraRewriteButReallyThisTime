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

            if(afs != null) {
                AvatarGroup newGroup = new AvatarGroup();

                //Import files from local directory into NBT compound.
                NbtCompound avatarCompound = new NbtCompound();
                afs.writeAvatarNBT(avatarCompound);

                FiguraAvatar localAvatar = new FiguraAvatar();
                FiguraAvatarNbtConverter.readNBT(localAvatar, avatarCompound);

                newGroup.avatars[0] = localAvatar;

                return newGroup;
            }
        }
        return null;
    }

    //Unused in local dealer
    @Override
    protected <T extends Entity> void requestForEntity(AvatarGroup group, T entity) {}
}
