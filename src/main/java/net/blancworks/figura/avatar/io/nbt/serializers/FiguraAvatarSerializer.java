package net.blancworks.figura.avatar.io.nbt.serializers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.io.converters.ModelMetadataConverter;
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

        List<JsonObject> bbmodels = collectBBModels(rootPath);

        //Process textures
        FiguraTextureGrouper textureGrouper = new FiguraTextureGrouper();
        NbtList textures = textureGrouper.serialize(bbmodels);
        avatarCompound.put("textures", textures);

        //Process models
        //Need to send the textureGrouper in, because we need to have a set of textures which persists between models
        //Parts also need to know which texture group they belong to.
        FiguraBBModelSerializer bbModelSerializer = new FiguraBBModelSerializer(textureGrouper);
        NbtCompound models = new NbtCompound();

        //Format "models" the same way as any other model part, for optimal recursion :)
        models.putString("name", "models");
        NbtList children = new NbtList();
        for (JsonObject bbmodel : bbmodels)
            children.add(bbModelSerializer.serialize(bbmodel));
        models.put("children", children);
        models.putString("render_mode", "cutout");

        //Get model metadata and inject it into the models
        Path metadataPath = getMetadataPath(rootPath);
        FiguraModelMetadata modelMetadata = ModelMetadataConverter.getInstance().convert(metadataPath);
        modelMetadata.inject(models);

        //Put the models in the final compound
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
            Path path = FiguraMod.getModDirectory().resolve("test3.moon");
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

    private static Path getMetadataPath(Path targetFolder) {
        Path result = targetFolder.resolve("avatar.json");
        if (!Files.exists(result))
            throw new RuntimeException("How are we reading this if there's no avatar.json? This is a bug!");
        return result;
    }
}
