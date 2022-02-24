package net.blancworks.figura.avatar.io.nbt.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.FiguraMod;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class FiguraAvatarSerializer implements FiguraNbtSerializer<Path, NbtCompound> {

    private static FiguraAvatarSerializer INSTANCE = new FiguraAvatarSerializer();

    public static FiguraAvatarSerializer getInstance() {
        return INSTANCE;
    }

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

        //Process scripts
        FiguraScriptsSerializer scriptsSerializer = new FiguraScriptsSerializer(rootPath);
        List<Path> luaSources = collectLuaSources(rootPath);
        NbtCompound scripts = scriptsSerializer.serialize(luaSources);
        avatarCompound.put("scripts", scripts);

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

    private static List<JsonObject> collectBBModels(Path targetFolder) {
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

    private static List<Path> collectLuaSources(Path targetFolder) {
        try {
            List<Path> result = new LinkedList<>();
            Files.walk(targetFolder).filter(p -> p.toString().toLowerCase().endsWith(".lua")).forEach(result::add);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
