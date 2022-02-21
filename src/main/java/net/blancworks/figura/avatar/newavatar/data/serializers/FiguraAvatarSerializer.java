package net.blancworks.figura.avatar.newavatar.data.serializers;

import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.nio.file.Path;
import java.util.List;

public class FiguraAvatarSerializer implements FiguraNbtSerializer<Path, NbtCompound> {

    @Override
    public NbtCompound serialize(Path rootPath) {
        NbtCompound avatarCompound = new NbtCompound();

        //Process textures
        FiguraTextureGrouper textureGrouper = new FiguraTextureGrouper();
        List<JsonObject> bbmodels = collectBBModels();
        NbtList textures = textureGrouper.serialize(bbmodels);
        avatarCompound.put("textures", textures);

        //Process models
        FiguraBBModelSerializer bbModelSerializer = new FiguraBBModelSerializer(textureGrouper);
        NbtCompound models = new NbtCompound();
        models.putString("name", "models");
        NbtList children = new NbtList();
        for (JsonObject bbmodel : bbmodels)
            children.add(bbModelSerializer.serialize(bbmodel));
        models.put("children", children);
        avatarCompound.put("models", models);

        //Process scripts (not dealing with that right now)


        //Put version
        avatarCompound.putString("version", "0.0.0");

        //Return it!
        return avatarCompound;
    }

    public List<JsonObject> collectBBModels() {
        return null;
    }
}
