package net.blancworks.figura.avatar.io.nbt.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blancworks.figura.utils.IOUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FiguraModelMetadata {

    private static final String SEPARATOR_REGEX = "\\.";
    private final Map<String, MetadataProperties> propertiesToApply;

    public FiguraModelMetadata(Map<String, JsonObject> propertiesMap) {
        propertiesToApply = new HashMap<>();
        for (Map.Entry<String, JsonObject> entry : propertiesMap.entrySet())
            propertiesToApply.put(entry.getKey(), MetadataProperties.of(entry.getValue()));
    }

    /**
     * Pass the "models" nbt compound in here, and the model metadata
     * will do its stuff to put some extra properties inside.
     * @param models The "models" nbt compound inside an avatar compound.
     */
    public void inject(NbtCompound models) {
        try {
            for (Map.Entry<String, MetadataProperties> entry : propertiesToApply.entrySet()) {
                entry.getValue().inject(getCompound(models, entry.getKey()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cringe method because I didn't store the children with keys :pensive:
     */
    private static NbtCompound getCompound(NbtCompound models, String key) throws IOException {
        String[] keys = key.split(SEPARATOR_REGEX);
        NbtCompound current = models;
        for (int i = 0; i < keys.length; i++) {
            if (current.contains("children")) {
                NbtList children = current.getList("children", NbtElement.COMPOUND_TYPE);
                for (int j = 0; j < children.size(); j++) {
                    NbtCompound child = children.getCompound(j);
                    if (child.getString("name").equals(keys[i])) {
                        current = child;
                        break;
                    }
                    if (j == children.size() - 1)
                        throw new IOException("Invalid part path: \"" + key + "\".");
                }
            } else
                throw new IOException("Invalid part path: \"" + key + "\".");
        }
        return current;
    }

    /**
     * Subclass!
     */
    private static record MetadataProperties(String renderMode, double funny) {

        private static MetadataProperties of(JsonObject json) {
            String renderMode = readStringProperty(json, "render_mode");
            double funnyNumber = readNumberProperty(json, "funny");

            return new MetadataProperties(renderMode, funnyNumber);
        }

        private static String readStringProperty(JsonObject json, String name) {
            if (json.has(name)) {
                JsonElement ele = json.get(name);
                if (ele.isJsonPrimitive()) {
                     return ele.getAsString();
                }
            }
            return null;
        }

        private static double readNumberProperty(JsonObject json, String name) {
            if (json.has(name)) {
                JsonElement ele = json.get(name);
                if (ele.isJsonPrimitive()) {
                    return ele.getAsDouble();
                }
            }
            return 0.0;
        }

        private void inject(NbtCompound modelPart) {
            IOUtils.storeString(modelPart, "render_mode", renderMode);
            IOUtils.storeSmallest(modelPart, "funny", funny);
        }

    }

}
