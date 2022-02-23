package net.blancworks.figura.avatar.newavatar.data.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.FiguraMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FiguraAvatarSerializer implements FiguraNbtSerializer<Path, NbtCompound> {

    @Override
    public NbtCompound serialize(Path rootPath) {
        NbtCompound avatarCompound = new NbtCompound();

        //Process textures
        FiguraTextureGrouper textureGrouper = new FiguraTextureGrouper();
        List<JsonObject> bbmodels = collectBBModels(rootPath);
        NbtList textures = textureGrouper.serialize(bbmodels);
        avatarCompound.put("textures", textures);

        //Process models
        //Need to send the textureGrouper in, because we need to have a set of textures which persists between models
        FiguraBBModelSerializer bbModelSerializer = new FiguraBBModelSerializer(textureGrouper);
        NbtCompound models = new NbtCompound();

        //Format "models" the same way as any other model part, for optimal recursion :)
        models.putString("name", "models");
        NbtList children = new NbtList();
        for (JsonObject bbmodel : bbmodels)
            children.add(bbModelSerializer.serialize(bbmodel));
        models.put("children", children);
        avatarCompound.put("models", models);

        //Process scripts (not dealing with that right now)


        //Put version
        avatarCompound.putString("version", "0.0.0");

        //TODO: remove
        //Testing code for outputting compressed compound
        try {
            Path path = FiguraMod.getModDirectory().resolve("test2.moon");
            if (!Files.exists(path))
                Files.createFile(path);
            NbtIo.writeCompressed(avatarCompound, path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Return it!
        return avatarCompound;
    }

    public List<JsonObject> collectBBModels(Path targetFolder) {
        try {
            List<JsonObject> result = new LinkedList<>();
            Files.walk(targetFolder).filter(p -> p.toString().toLowerCase().endsWith(".bbmodel")).forEach(path -> {
                try {
                    result.add(JsonParser.parseString(Files.readString(path)).getAsJsonObject());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
