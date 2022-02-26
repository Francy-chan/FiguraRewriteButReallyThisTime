package net.blancworks.figura.avatar.io.nbt.deserializers;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class FiguraAvatarDeserializer implements FiguraNbtDeserializer<FiguraAvatar, NbtCompound> {

    private static final FiguraAvatarDeserializer INSTANCE = new FiguraAvatarDeserializer();

    public static FiguraAvatarDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public FiguraAvatar deserialize(NbtCompound data) {

        NbtList textures = data.getList("textures", NbtElement.COMPOUND_TYPE);
        BufferSetBuilder bufferSetBuilder = BufferSetBuilderDeserializer.getInstance().deserialize(textures);

        FiguraModelPart rootPart = new FiguraModelPartDeserializer(bufferSetBuilder).deserialize(data.getCompound("models"));
        rootPart.getTransform().scale.x = rootPart.getTransform().scale.y = rootPart.getTransform().scale.z = 1.0/16;

        FiguraScriptEnvironment scriptEnvironment = FiguraScriptsDeserializer.getInstance().deserialize(data.getCompound("scripts"));

        return new FiguraAvatar(bufferSetBuilder.build(), rootPart, scriptEnvironment);
    }
}
