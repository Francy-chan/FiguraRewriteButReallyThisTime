package net.blancworks.figura.avatar.newavatar.data.deserializers;

import net.blancworks.figura.avatar.newavatar.NewFiguraAvatar;
import net.blancworks.figura.avatar.newavatar.NewFiguraModelPart;
import net.blancworks.figura.avatar.newavatar.data.BufferSetBuilder;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class FiguraAvatarDeserializer implements FiguraNbtDeserializer<NewFiguraAvatar, NbtCompound> {

    @Override
    public NewFiguraAvatar deserialize(NbtCompound data) {

        NbtList textures = data.getList("textures", NbtElement.COMPOUND_TYPE);
        BufferSetBuilder bufferSetBuilder = new BufferSetBuilderDeserializer().deserialize(textures);

        NewFiguraModelPart rootPart = new FiguraModelPartDeserializer(bufferSetBuilder).deserialize(data.getCompound("models"));
        rootPart.transform.scale.x = rootPart.transform.scale.y = rootPart.transform.scale.z = 1.0/16;

        return new NewFiguraAvatar(bufferSetBuilder.build(), rootPart);
    }
}
