package net.blancworks.figura.avatar.components;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.nbt.NbtCompound;

public abstract class FiguraAvatarComponent {
    public FiguraAvatar ownerAvatar;

    public FiguraAvatarComponent(FiguraAvatar owner){
        this.ownerAvatar = owner;
    }

    // -- IO --
    public abstract void readFromNBT(NbtCompound tag);
    public abstract void writeToNBT(NbtCompound tag);
}
