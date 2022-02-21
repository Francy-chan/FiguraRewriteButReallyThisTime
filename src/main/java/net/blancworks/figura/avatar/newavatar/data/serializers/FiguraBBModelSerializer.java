package net.blancworks.figura.avatar.newavatar.data.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FiguraBBModelSerializer implements FiguraNbtSerializer<JsonObject, NbtCompound> {

    private final FiguraTextureGrouper textureGrouper;
    private Map<String, JsonObject> elements;
    private String modelName;
    private List<String> textureNames;

    public FiguraBBModelSerializer(FiguraTextureGrouper textureGrouper) {
        this.textureGrouper = textureGrouper;
    }

    @Override
    public NbtCompound serialize(JsonObject bbmodel) {
        elements = collectElementsByUUID(bbmodel);
        modelName = bbmodel.get("name").getAsString();
        textureNames = new ArrayList<>();
        bbmodel.getAsJsonArray("textures").getAsJsonArray().forEach(texElement -> textureNames.add(texElement.getAsJsonObject().get("name").getAsString()));

        NbtCompound result = new NbtCompound();
        result.putString("name", modelName);
        result.put("children", processParts(bbmodel.get("outliner").getAsJsonArray()));

        
        return result;
    }

    private NbtList processParts(JsonArray parts) {
        NbtList result = new NbtList();
        for (JsonElement element : parts) {
            NbtCompound partNbt = new NbtCompound();
            if (element.isJsonObject()) {
                //Part is a group
                JsonObject partJson = element.getAsJsonObject();

                partNbt.putString("name", partJson.get("name").getAsString());
                storeVec3(partNbt, partJson, "origin");
                storeVec3(partNbt, partJson, "rotation");
                if (partJson.has("visibility"))
                    partNbt.putBoolean("visibility", partJson.get("visibility").getAsBoolean());
                if (partJson.has("children"))
                    partNbt.put("children", processParts(partJson.getAsJsonArray("children")));

            } else {
                //Part is not a group
                JsonObject partJson = elements.get(element.getAsString());
                partNbt.putString("name", partJson.get("name").getAsString());
                storeVec3(partNbt, partJson, "origin");
                storeVec3(partNbt, partJson, "rotation");
                if (partJson.has("visibility"))
                    partNbt.putBoolean("visibility", partJson.get("visibility").getAsBoolean());

                if (partJson.has("from")) {
                    //Part is a cuboid
                    processCuboid(partNbt, partJson);
                } else {
                    //Part is a mesh
                    processMesh(partNbt, partJson);
                }
            }
            result.add(partNbt);
        }
        return result;
    }

    private static final String[] faceNames = new String[] {
            "north",
            "south",
            "east",
            "west",
            "up",
            "down"
    };

    private void processCuboid(NbtCompound nbt, JsonObject json) {
        storeVec3(nbt, json, "from");
        storeVec3(nbt, json, "to");
        storeSmallest(nbt, "inflate", json.get("inflate").getAsFloat());

        if (json.has("faces")) {
            NbtCompound facesNbt = new NbtCompound();
            JsonObject facesJson = json.getAsJsonObject("faces");
            for (String faceName : faceNames) {
                if (facesJson.has(faceName)) {
                    JsonObject faceJson = facesJson.getAsJsonObject(faceName);
                    if (faceJson.has("texture")) {
                        int texture = faceJson.get("texture").getAsInt();
                        if (texture >= 0) {

                            NbtCompound faceNbt = new NbtCompound();

                            if (faceJson.has("uv")) {
                                JsonArray uv = faceJson.getAsJsonArray("uv");
                                storeSmallest(faceNbt, "u1", uv.get(0).getAsFloat());
                                storeSmallest(faceNbt, "v1", uv.get(1).getAsFloat());
                                storeSmallest(faceNbt, "u2", uv.get(2).getAsFloat());
                                storeSmallest(faceNbt, "v2", uv.get(3).getAsFloat());
                            }

                            int mappedTexture = textureGrouper.getTextureIndex(modelName, textureNames.get(texture));
                            storeSmallest(faceNbt, "texture", mappedTexture);

                            if (faceJson.has("rotation"))
                                storeSmallest(faceNbt, "rotation", faceJson.get("rotation").getAsFloat());

                            facesNbt.put(faceName, faceNbt);
                        }
                    }
                }
            }
        }
    }

    private void processMesh(NbtCompound nbt, JsonObject json) {
        //Nothing for now
    }

    private static void storeVec3(NbtCompound nbt, JsonObject json, String name) {
        if (json.has(name)) {
            JsonArray arr = json.getAsJsonArray(name);
            storeSmallest(nbt, name+"X", arr.get(0).getAsFloat());
            storeSmallest(nbt, name+"Y", arr.get(1).getAsFloat());
            storeSmallest(nbt, name+"Z", arr.get(2).getAsFloat());
        }
    }

    private static void storeSmallest(NbtCompound nbt, String name, float value) {
        if (Math.rint(value) == value) {
            if (value <= 127 && value >= 128)
                nbt.putByte(name, (byte) value);
            else if (value <= 32767 && value >= -32768)
                nbt.putShort(name, (short) value);
            else
                nbt.putFloat(name, value);
        } else {
            nbt.putFloat(name, value);
        }
    }

    private static Map<String, JsonObject> collectElementsByUUID(JsonObject bbmodel) {
        Map<String, JsonObject> result = new HashMap<>();
        JsonArray elements = bbmodel.getAsJsonArray("elements");
        for (JsonElement element : elements) {
            JsonObject elementObj = element.getAsJsonObject();
            String uuid = elementObj.get("uuid").getAsString();
            result.put(uuid, elementObj);
        }
        return result;
    }
}
