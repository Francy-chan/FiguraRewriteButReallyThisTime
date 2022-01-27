package net.blancworks.figura.avatar.components.model;

import net.minecraft.nbt.NbtCompound;

public class FiguraMeshModelPart extends FiguraModelPart {

    public FiguraMeshModelPart() {
        type = 2;
    }

    @Override
    public void readFromNBT(NbtCompound tag) {
        super.readFromNBT(tag);
    }
}
