package net.blancworks.figura.avatar.components.texture;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;

public class FiguraTextureGroupManager extends FiguraAvatarComponent<NbtList> {
    public final ArrayList<FiguraTextureGroup> sets = new ArrayList<>();

    public FiguraTextureGroupManager(FiguraAvatar owner) {
        super(owner);
    }

    @Override
    public void readFromNBT(NbtList tag) {
        for (int i = 0; i < tag.size(); i++) {
            NbtCompound setNbt = tag.getCompound(i);
            FiguraTextureGroup set = new FiguraTextureGroup();

            set.readNBT(setNbt);
            sets.add(set);
        }
    }

    /**
     * Holds figura textures used for rendering.
     */
    public static class FiguraTextureGroup {
        public FiguraTexture main;
        public FiguraTexture emissive;

        public void use(FiguraAvatar avatar) {
            if (main != null) main.registerIfNeeded(avatar);
            if (emissive != null) emissive.registerIfNeeded(avatar);
        }

        public void readNBT(NbtCompound compound) {
            main = readTexture(compound, "main");
            emissive = readTexture(compound, "emissive");
        }

        private FiguraTexture readTexture(NbtCompound compound, String key) {
            if (!compound.contains(key, NbtElement.BYTE_ARRAY_TYPE)) return null;

            FiguraTexture texture = new FiguraTexture();
            texture.readFromNBT((NbtByteArray) compound.get(key));
            return texture;
        }
    }
}
