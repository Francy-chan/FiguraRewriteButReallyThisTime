package net.blancworks.figura.avatar.reader;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.model.FiguraModel;
import net.blancworks.figura.avatar.components.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.components.FiguraTextureSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

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
    public void readNBT(FiguraAvatar avatar, NbtCompound tag) {

        if (tag.contains("model", NbtElement.COMPOUND_TYPE)) {
            NbtCompound modelCompound = tag.getCompound("model");

            avatar.model = new FiguraModel(avatar);
            avatar.model.readFromNBT(modelCompound);
        }

        if (tag.contains("scripts", NbtElement.COMPOUND_TYPE)) {
            NbtCompound scriptCompound = tag.getCompound("scripts");

            avatar.scriptEnv = new FiguraScriptEnvironment(avatar);
            avatar.scriptEnv.readFromNBT(scriptCompound);
        }

        if (tag.contains("textures", NbtElement.COMPOUND_TYPE)) {
            NbtCompound textureCompound = tag.getCompound("textures");

            avatar.textures = new FiguraTextureSet(avatar);
            avatar.textures.readFromNBT(textureCompound);
        }
    }

    public void writeNBT(FiguraAvatar avatar, NbtCompound tag) {
        if (avatar.model != null) {
            NbtCompound modelCompound = new NbtCompound();
            avatar.model.writeToNBT(modelCompound);

            tag.put("model", modelCompound);
        }

        if (avatar.scriptEnv != null) {
            NbtCompound modelCompound = new NbtCompound();
            avatar.scriptEnv.writeToNBT(modelCompound);

            tag.put("scripts", modelCompound);
        }

        if (avatar.textures != null) {
            NbtCompound modelCompound = new NbtCompound();
            avatar.textures.writeToNBT(modelCompound);

            tag.put("textures", modelCompound);
        }
    }

}
