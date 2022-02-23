package net.blancworks.figura.avatar.newavatar.data.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blancworks.figura.FiguraMod;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class FiguraTextureGrouper implements FiguraNbtSerializer<List<JsonObject>, NbtList> {

    //Map of bbmodel names to maps of texture names to buffers.
    private final HashMap<String, HashMap<String, TextureSet>> setsByTextureByModel;
    //Contains the same protobuffers as the above map, just kept for easy iterating in order
    private final List<TextureSet> textureSets;

    public FiguraTextureGrouper() {
        setsByTextureByModel = new HashMap<>();
        textureSets = new LinkedList<>();
    }

    @Override
    public NbtList serialize(List<JsonObject> bbmodels) {
        for (JsonObject bbmodel : bbmodels)
            readBBModelTextures(bbmodel);
        return getTextureList();
    }

    private void readBBModelTextures(JsonObject bbmodel) {
        HashMap<String, TextureSet> modelMap = new HashMap<>();
        String modelName = bbmodel.get("name").getAsString();
        if (setsByTextureByModel.containsKey(modelName))
            FiguraMod.LOGGER.error("Tried to load two models with the same name! Name: " + modelName);
        if (bbmodel.has("textures")) {
            for (JsonElement textureElement : bbmodel.getAsJsonArray("textures")) {

                //Get the name and extension for this texture
                JsonObject texture = (JsonObject) textureElement;
                String name = texture.get("name").getAsString().replace(".png", "");
                String[] parts = name.split("_");
                String extension;
                String truncatedName; //Name without the extension
                if (parts.length == 1) {
                    extension = "";
                    truncatedName = name;
                }
                else {
                    extension = parts[parts.length-1];
                    truncatedName = name.substring(0, name.length()-extension.length()-1);
                }


                //Now we know the name and the extension. We can add this texture to the map now:

                if (!modelMap.containsKey(truncatedName)) {
                    TextureSet newTextureSet = new TextureSet(textureSets.size());
                    textureSets.add(newTextureSet);
                    modelMap.put(truncatedName, newTextureSet);
                }
                if (!name.equals(truncatedName))
                    modelMap.put(name, modelMap.get(truncatedName));


                String source = texture.get("source").getAsString().replace("data:image/png;base64,", "");
                byte[] bytes = Base64.getDecoder().decode(source);
                switch (extension) {
                    case "e" -> modelMap.get(name).setEmissive(bytes);
                    default -> modelMap.get(name).setMain(bytes);
                }
            }
        }
        setsByTextureByModel.put(modelName, modelMap);
    }

    private NbtList getTextureList() {
        NbtList result = new NbtList();
        for (TextureSet textureSet : textureSets) {
            NbtCompound compound = new NbtCompound();
            if (textureSet.main != null)
                compound.put("main", new NbtByteArray(textureSet.main));
            if (textureSet.emissive != null)
                compound.put("emissive", new NbtByteArray(textureSet.emissive));
            result.add(compound);
        }
        return result;
    }

    public int getTextureIndex(String modelName, String texName) {
        return setsByTextureByModel.get(modelName).get(texName).index;
    }

    private static class TextureSet {

        private byte[] main;
        private byte[] emissive;
        private final int index;

        private TextureSet(int index) {
            this.index = index;
        }

        public void setMain(byte[] bytes) {
            if (main != null)
                FiguraMod.LOGGER.error("Two textures with the same name!");
            main = bytes;
        }

        public void setEmissive(byte[] bytes) {
            if (emissive != null)
                FiguraMod.LOGGER.error("Two textures with the same name!");
            emissive = bytes;
        }

    }

}
