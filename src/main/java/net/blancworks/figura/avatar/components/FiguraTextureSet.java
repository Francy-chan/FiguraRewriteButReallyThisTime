package net.blancworks.figura.avatar.components;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.TextureUtil;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.nbt.NbtCompound;

import java.util.Map;

public class FiguraTextureSet extends FiguraAvatarComponent {

    public Map<String, FiguraTexture> textures;

    public FiguraTextureSet(FiguraAvatar owner) {
        super(owner);
    }

    @Override
    public void readFromNBT(NbtCompound tag) {
        ImmutableMap.Builder<String, FiguraTexture> mapBuilder = new ImmutableMap.Builder<>();

        //Read textures from NBT
        for (String key : tag.getKeys()) {
            NbtCompound textureTag = tag.getCompound(key);
            FiguraTexture texture = new FiguraTexture(ownerAvatar);
            texture.readFromNBT(textureTag);
        }

        textures = mapBuilder.build();
    }

    @Override
    public void writeToNBT(NbtCompound tag) {

        //Write textures to NBT
        for (Map.Entry<String, FiguraTexture> entry : textures.entrySet()) {
            NbtCompound textureTag = new NbtCompound();
            entry.getValue().writeToNBT(textureTag);
            tag.put(entry.getKey(), textureTag);
        }
    }
}
