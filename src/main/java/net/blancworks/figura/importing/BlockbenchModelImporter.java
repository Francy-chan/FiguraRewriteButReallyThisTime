package net.blancworks.figura.importing;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles importing .bbmodel files and converting them to an NBT tag for the avatar.
 */
public class BlockbenchModelImporter implements FileImporter {
    private static final JsonParser jsonParser = new JsonParser();

    /**
     * This is a map of ID -> ID that's applied (when appropriate) when translating from JSON to NBT
     */
    private static final ImmutableMap<String, String> idRemapMap = new ImmutableMap.Builder<String, String>()
            .put("visibility", "visible")
            .build();


    // -- Importer --
    @Override
    public boolean importFiles(Path dir, NbtCompound target) {
        boolean r = false;

        try {
            //Collect .bbmodel files
            List<Path> bbmodelFiles = Files.walk(dir).filter(p -> p.toString().toLowerCase().endsWith(".bbmodel")).collect(Collectors.toList());

            //Import them one by one
            for (Path file : bbmodelFiles)
                r |= importBBModelFile(file, target);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }


    // -- Helper Functions --

    public boolean importBBModelFile(Path filePath, NbtCompound target) throws Exception {
        String s = Files.readString(filePath);

        JsonObject rootObject = (JsonObject) jsonParser.parse(s);

        //Parse out the elements of the json file.
        NbtCompound elementList = new NbtCompound();
        parseElements(elementList, rootObject.get("elements").getAsJsonArray());

        //Parse out the outliner
        NbtCompound outliner = new NbtCompound();
        parseOutliner(outliner, rootObject.get("outliner").getAsJsonArray());


        // ---> Convert outliner + elements into nbt model <---



        return true;
    }


    // - Elements -

    private void parseElements(NbtCompound target, JsonArray elementArray) {

        for (JsonElement element : elementArray) {
            //Parse element
            NbtCompound elementCompound = new NbtCompound();
            parseElement(elementCompound, element.getAsJsonObject());

            //Pull UUID out of element NBT
            String idString = elementCompound.getString("uuid");
            elementCompound.remove("uuid");

            //Put element into nbt by UUID
            target.put(idString, elementCompound);
        }
    }

    private void parseElement(NbtCompound target, JsonObject elementObject) {
        // Strings
        String type = transferString("type", elementObject, target);
        String name = transferString("name", elementObject, target);
        String uuid = transferString("uuid", elementObject, target);

        //Floats
        Float inflate = transferFloat("inflate", elementObject, target);

        //Booleans
        Boolean locked = transferBoolean("locked", elementObject, target);
        Boolean visible = transferBoolean("visibility", elementObject, target);

        //Float arrays
        float[] origin = transferFloatArray("origin", elementObject, target);
        float[] uv_offset = transferFloatArray("uv_offset", elementObject, target);

        if (type == null)
            parseCuboidElement(target, elementObject);
        else if (type.equals("mesh"))
            parseMeshElement(target, elementObject);
    }

    /**
     * Parses a normal cuboid element from the JSON.
     *
     * @param target        The target NBT Compound to write the properties to
     * @param elementObject The Json element to read from.
     */
    private void parseCuboidElement(NbtCompound target, JsonObject elementObject) {
        float[] from = transferFloatArray("from", elementObject, target);
        float[] to = transferFloatArray("to", elementObject, target);

        // -- Parse faces --
        NbtCompound faceList = new NbtCompound();
        target.put("faces", faceList);

        JsonObject faceJsonObject = elementObject.get("faces").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : faceJsonObject.entrySet()) {
            NbtCompound faceCompound = new NbtCompound();
            parseFace(faceCompound, entry.getValue().getAsJsonObject());

            faceList.put(entry.getKey(), faceCompound);
        }
    }

    private void parseFace(NbtCompound target, JsonObject elementObject) {
        float[] uv = transferFloatArray("uv", elementObject, target);
        int texture = transferInteger("texture", elementObject, target);
    }

    /**
     * Parses a mesh element from the JSON.
     *
     * @param target        The target NBT Compound to write the properties to
     * @param elementObject The Json element to read from.
     */
    private void parseMeshElement(NbtCompound target, JsonObject elementObject) {
        // -- Parse Vertices --
        NbtCompound vertList = new NbtCompound();
        target.put("vertices", vertList);

        JsonObject vertJsonObject = elementObject.get("vertices").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : vertJsonObject.entrySet()) {
            float[] vertPos = transferFloatArray(entry.getKey(), vertJsonObject, vertList);
        }

        // -- Parse Faces --
        NbtCompound faceList = new NbtCompound();
        target.put("faces", faceList);

        JsonObject faceJsonObject = elementObject.get("faces").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : faceJsonObject.entrySet()) {
            NbtCompound faceCompound = new NbtCompound();
            parseMeshFace(faceCompound, entry.getValue().getAsJsonObject());

            faceList.put(entry.getKey(), faceCompound);
        }
    }

    private void parseMeshFace(NbtCompound target, JsonObject elementObject) {
        // -- Parse UV --
        NbtCompound uvList = new NbtCompound();
        target.put("uv", uvList);

        JsonObject uvJsonObject = elementObject.get("uv").getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : uvJsonObject.entrySet())
            transferFloatArray(entry.getKey(), uvJsonObject, uvList);

        // -- Parse vertices --
        String[] vertices = transferStringArray("vertices", elementObject, target);
    }

    // - Outliner -

    /**
     * Parses the outliner from a given JSON array.
     *
     * @param target        The NBT list to put the outliner into
     * @param outlinerArray The json object containing the outliner
     */
    private void parseOutliner(NbtCompound target, JsonArray outlinerArray) {
        for (JsonElement element : outlinerArray) {

            //Assume json objects are folders, and otherwise it's a uuid for a cube.
            if (element.isJsonObject()) {
                NbtCompound folderCompound = new NbtCompound();

                parseOutlinerEntry(folderCompound, element.getAsJsonObject());

                target.put(folderCompound.get("uuid").asString(), folderCompound);
            } else {
                String s = element.getAsString();

                target.put(s, NbtString.of(s));
            }
        }
    }

    private void parseOutlinerEntry(NbtCompound target, JsonObject elementObject) {
        String name = transferString("name", elementObject, target);
        String uuid = transferString("uuid", elementObject, target);

        float[] origin = transferFloatArray("origin", elementObject, target);
        int color = transferInteger("color", elementObject, target);
        int autoUV = transferInteger("autouv", elementObject, target);

        boolean export = transferBoolean("export", elementObject, target);
        boolean isOpen = transferBoolean("isOpen", elementObject, target);
        boolean locked = transferBoolean("locked", elementObject, target);
        boolean visibility = transferBoolean("visibility", elementObject, target);

        String[] children = transferStringArray("children", elementObject, target);
    }

    // -- JSON helpers --

    //--Float
    private static Float transferFloat(String key, JsonObject from, NbtCompound to) {
        Float get = getFloatOrNull(from, key);
        if (get != null)
            to.putFloat(key, get);

        return get;
    }

    private static Float getFloatOrNull(JsonObject object, String key) {
        if (object.has(key))
            return object.get(key).getAsFloat();
        return null;
    }

    private static float[] transferFloatArray(String key, JsonObject from, NbtCompound to) {
        float[] get = getFloatArrayOrNull(from, key);
        if (get != null) {
            NbtList floatList = new NbtList();
            for (float v : get)
                floatList.add(NbtFloat.of(v));
            to.put(key, floatList);
        }

        return get;
    }

    private static float[] getFloatArrayOrNull(JsonObject object, String key) {
        if (object.has(key)) {
            JsonArray array = object.get(key).getAsJsonArray();

            List<Float> floatList = new ArrayList<>();
            array.forEach(e -> {
                floatList.add(e.getAsFloat());
            });

            float[] fa = new float[floatList.size()];
            for (int i = 0; i < floatList.size(); i++) {
                fa[i] = floatList.get(i);
            }

            return fa;
        }
        return null;
    }

    //--Integer
    private static Integer transferInteger(String key, JsonObject from, NbtCompound to) {
        Integer get = getIntegerOrNull(from, key);
        if (get != null) {
            to.putInt(key, get);
            return get;
        }

        return 0;
    }

    private static Integer getIntegerOrNull(JsonObject object, String key) {
        if (object.has(key))
            return object.get(key).getAsInt();
        return null;
    }


    private static int[] transferIntegerArray(String key, JsonObject from, NbtCompound to) {
        int[] get = getIntegerArrayOrNull(from, key);
        if (get != null)
            to.putIntArray(key, get);

        return get;
    }

    private static int[] getIntegerArrayOrNull(JsonObject object, String key) {
        if (object.has(key)) {
            JsonArray array = object.get(key).getAsJsonArray();

            List<Integer> intList = new ArrayList<>();
            array.forEach(e -> {
                intList.add(e.getAsInt());
            });

            return Arrays.stream(intList.toArray()).mapToInt(i -> i == null ? 0 : (int) i).toArray();
        }
        return null;
    }

    //--Boolean
    private static Boolean transferBoolean(String key, JsonObject from, NbtCompound to) {
        Boolean get = getBooleanOrNull(from, key);
        if (get != null) {
            to.putBoolean(key, get);
            return get;
        }

        return false;
    }

    private static Boolean getBooleanOrNull(JsonObject object, String key) {
        if (object.has(key))
            return object.get(key).getAsBoolean();
        return null;
    }


    //--String
    private static String transferString(String key, JsonObject from, NbtCompound to) {
        String get = getStringOrNull(from, key);
        if (get != null)
            to.putString(key, get);

        return get;
    }

    private static String[] transferStringArray(String key, JsonObject from, NbtCompound to) {
        String[] get = getStringArrayOrNull(from, key);
        if (get != null) {
            NbtList stringList = new NbtList();
            for (String s : get)
                stringList.add(NbtString.of(s));
            to.put(key, stringList);
        }

        return get;
    }


    private static String getStringOrNull(JsonObject object, String key) {
        if (object.has(key))
            return object.get(key).getAsString();
        return null;
    }

    private static String[] getStringArrayOrNull(JsonObject object, String key) {
        if (object.has(key)) {
            JsonArray array = object.get(key).getAsJsonArray();

            List<String> stringList = new ArrayList<>();
            array.forEach(e -> {
                stringList.add(e.getAsString());
            });

            return stringList.toArray(new String[0]);
        }
        return null;
    }

}
