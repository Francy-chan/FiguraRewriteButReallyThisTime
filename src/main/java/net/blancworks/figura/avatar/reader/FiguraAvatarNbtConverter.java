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
     * Supports models, scripts, and textures.
     */
    public static void readNBT(FiguraAvatar avatar, NbtCompound tag) {

        if (tag.contains("models", NbtElement.LIST_TYPE)) {
            NbtList modelList = tag.getList("models", NbtElement.COMPOUND_TYPE);
            avatar.models.readFromNBT(modelList);
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