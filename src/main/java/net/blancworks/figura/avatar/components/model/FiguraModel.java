package net.blancworks.figura.avatar.components.model;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.minecraft.nbt.NbtCompound;

public class FiguraModel extends FiguraAvatarComponent {
    /**
     * Holds all the model parts contained within this model
     */
    public final FiguraModelPart rootPart = new FiguraModelPart();

    public FiguraModel(FiguraAvatar owner) {
        super(owner);
    }


    // -- IO --
    @Override
    public void readFromNBT(NbtCompound tag) {
        rootPart.readFromNBT(tag.getCompound("root"));
    }

    @Override
    public void writeToNBT(NbtCompound tag) {
        NbtCompound rootTag = new NbtCompound();
        rootPart.writeToNBT(rootTag);
        tag.put("root", rootTag);
    }
}
