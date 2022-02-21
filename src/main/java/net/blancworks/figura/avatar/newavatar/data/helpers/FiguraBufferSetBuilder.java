package net.blancworks.figura.avatar.newavatar.data.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.newavatar.FiguraBuffer;
import net.blancworks.figura.avatar.newavatar.FiguraBufferSet;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FiguraBufferSetBuilder {

    //Map of bbmodel names to maps of texture names to buffers.
    private final HashMap<String, HashMap<String, ProtoBuffer>> buffersByTextureByModel;
    //Contains the same protobuffers as the above map, just kept for easy iterating in order
    private final List<ProtoBuffer> protoBuffers;

    public FiguraBufferSetBuilder() {
        buffersByTextureByModel = new HashMap<>();
        protoBuffers = new LinkedList<>();
    }

    public void readBBModelTextures(JsonObject bbmodel) {
        HashMap<String, ProtoBuffer> modelMap = new HashMap<>();
        String modelName = bbmodel.get("name").getAsString();
        if (buffersByTextureByModel.containsKey(modelName))
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
                    ProtoBuffer newBuffer = new ProtoBuffer();
                    protoBuffers.add(newBuffer);
                    modelMap.put(truncatedName, newBuffer);
                }
                if (!name.equals(truncatedName))
                    modelMap.put(name, modelMap.get(truncatedName));


                String source = texture.get("source").getAsString().replace("data:image/png;base64,", "");
                byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
                switch (extension) {
                    case "e" -> modelMap.get(name).setEmissive(bytes);
                    default -> modelMap.get(name).setMain(bytes);
                }
            }
        }
        buffersByTextureByModel.put(modelName, modelMap);
    }

    public NbtList getTextureList() {
        NbtList result = new NbtList();
        for (ProtoBuffer buffer : protoBuffers) {
            NbtCompound textureSet = new NbtCompound();
            if (buffer.main != null)
                textureSet.put("main", new NbtByteArray(buffer.main));
            if (buffer.emissive != null)
                textureSet.put("emissive", new NbtByteArray(buffer.emissive));
            result.add(textureSet);
        }
        return result;
    }

    public void addVertex(String modelName, String textureName, float x, float y, float z, float u, float v, byte normal) {
        buffersByTextureByModel.get(modelName).get(textureName).addVertex(x, y, z, u, v, normal);
    }

    public FiguraBufferSet buildBufferSet() {
        return null;
    }

    private static class ProtoBuffer {

        private byte[] main;
        private byte[] emissive;
        private final List<FiguraBuffer.FiguraVertex> vertexList = new LinkedList<>();

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

        public void addVertex(float x, float y, float z, float u, float v, byte normal) {
            vertexList.add(FiguraBuffer.FiguraVertex.get(x, y, z, u, v, normal));
        }

        public FiguraBuffer buildBuffer() {
            return null;
        }

    }

}
