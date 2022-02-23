package net.blancworks.figura.avatar.components;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.nbt.NbtElement;

public abstract class FiguraAvatarComponent<T extends NbtElement> {
    public FiguraAvatar ownerAvatar;

    public FiguraAvatarComponent(FiguraAvatar owner){
        this.ownerAvatar = owner;
    }

    public void destroy(){};

    // -- IO --
    public abstract void readFromNBT(T tag);
}
