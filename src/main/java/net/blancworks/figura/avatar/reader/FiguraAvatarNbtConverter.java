package net.blancworks.figura.avatar.reader;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/**
 * The base figura avatar reader.
 * The idea behind this class is that the current version of the mod has the highest version of this class instantiated.
 * When an avatar is loaded, it's loaded bottom-up, from the oldest version to the newest.
 * Any appropriate NBT tags are filled in where possible from each level.
 * <p>
 * For writing, only the highest-level system writes, where appropriate.
 */
public abstract class FiguraAvatarNbtConverter {

    /**
     * Reads an avatar from an NBT compound.
     * <p>
     * Supports 1 model, any count of scripts, and any count of textures
     */
    public static void readNBT(FiguraAvatar avatar, NbtCompound tag) {

        if (tag.contains("model", NbtElement.COMPOUND_TYPE)) {
            NbtCompound modelCompound = tag.getCompound("model");
            avatar.model.readFromNBT(modelCompound);
        }

        if (tag.contains("scripts", NbtElement.COMPOUND_TYPE)) {
            NbtCompound scriptCompound = tag.getCompound("scripts");
            avatar.scriptEnv.readFromNBT(scriptCompound);
        }

        if (tag.contains("textures", NbtElement.LIST_TYPE)) {
            NbtList texturesList = tag.getList("textures", NbtElement.COMPOUND_TYPE);
            avatar.textureGroupManager.readFromNBT(texturesList);
        }
    }
}


//e