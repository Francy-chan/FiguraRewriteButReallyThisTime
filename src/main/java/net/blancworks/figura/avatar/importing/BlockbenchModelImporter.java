package net.blancworks.figura.avatar.importing;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.nbt.*;
import org.apache.logging.log4j.util.TriConsumer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
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

    private static final ImmutableMap<String, TriConsumer<JsonObject, NbtCompound, List<Integer>>> elementConverters = new ImmutableMap.Builder<String, TriConsumer<JsonObject, NbtCompound, List<Integer>>>()
            .put("cuboid", BlockbenchModelImporter::convertCuboidElement)
            .put("mesh", BlockbenchModelImporter::convertMeshElement)
            .build();

    private static final ImmutableMap<String, String> textureSetBuilders = new ImmutableMap.Builder<String, String>()
            .put("", "main")
            .put("e", "emissive")
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

    public static boolean importBBModelFile(Path filePath, NbtCompound target) throws Exception {
        String s = Files.readString(filePath);

        JsonObject rootObject = (JsonObject) jsonParser.parse(s);

        // -- Textures --
        List<Integer> textureGroupMappings = new ArrayList<>();
        JsonArray textureArray = rootObject.getAsJsonArray("textures");
        target.put("textures", convertTextures(textureArray, textureGroupMappings));

        // -- Model --
        ImmutableMap<UUID, JsonObject> elementsByUUID = sortElements(rootObject.getAsJsonArray("elements"));

        JsonArray outlinerArray = rootObject.getAsJsonArray("outliner");

        NbtCompound modelCompound = new NbtCompound();
        convertEntryList(outlinerArray, elementsByUUID, textureGroupMappings, modelCompound);
        target.put("model", modelCompound);


        return true;
    }


    /**
     * Builds a map of UUID -> Json Object elements from the element list provided in the blockbench model
     */
    public static ImmutableMap<UUID, JsonObject> sortElements(JsonArray object) {
        ImmutableMap.Builder<UUID, JsonObject> builder = new ImmutableMap.Builder<>();

        for (JsonElement element : object) {
            JsonObject elementObject = element.getAsJsonObject();
            builder.put(UUID.fromString(elementObject.get("uuid").getAsString()), elementObject);
        }

        return builder.build();
    }


    //Converts a list of outliner entries (either outliner objects or elements) to an NBT List
    public static NbtCompound convertEntryList(JsonArray array, Map<UUID, JsonObject> objectMap, List<Integer> textureGroupMappings) {
        return convertEntryList(array, objectMap, textureGroupMappings, new NbtCompound());
    }

    //Converts a list of outliner entries (either outliner objects or elements) to an NBT List
    public static NbtCompound convertEntryList(JsonArray array, Map<UUID, JsonObject> objectMap, List<Integer> textureGroupMappings, NbtCompound list) {
        //Iterate over each child
        for (JsonElement childElement : array) {
            NbtCompound entry = null;

            //Get & convert entry
            if (childElement.isJsonObject()) { //Entry is another outliner entry
                //Convert and add child outliner entry
                entry = convertOutlinerEntry(childElement.getAsJsonObject(), objectMap, textureGroupMappings);
            } else { //Entry is an element
                //Get JsonObject from map
                UUID elementUUID = UUID.fromString(childElement.getAsString());
                JsonObject childObject = objectMap.get(elementUUID);

                //Convert element
                if (childObject != null) {
                    entry = convertElement(childObject, textureGroupMappings);
                }
            }

            //Conversion failed or whatever
            if (entry == null)
                continue;

            //Put entry into NBT
            String name = entry.getString("name");
            list.put(name, entry);
        }

        return list;
    }


    /**
     * Takes a json outliner entry and converts it to NBT (acts recursively on children of the entry
     */
    public static NbtCompound convertOutlinerEntry(JsonObject entry, Map<UUID, JsonObject> objectMap, List<Integer> textureGroupMappings) {
        NbtCompound entryCompound = new NbtCompound();
        transferCommon(entry, entryCompound);

        //Children!
        entryCompound.put("children", convertEntryList(entry.getAsJsonArray("children"), objectMap, textureGroupMappings));

        entryCompound.putInt("type", 0);

        return entryCompound;
    }

    /**
     * Converts a json element object to NBT.
     */
    public static NbtCompound convertElement(JsonObject element, List<Integer> textureGroupMappings) {
        NbtCompound elementCompound = new NbtCompound();
        transferCommon(element, elementCompound);
        transferFloatArray(element, elementCompound, "uv_offset");

        //Get element type
        String type = "cuboid";
        if (element.has("type")) type = element.get("type").getAsString();

        //Run converter on element
        var converter = elementConverters.get(type);
        if (converter != null) converter.accept(element, elementCompound, textureGroupMappings);

        return elementCompound;
    }


    public static NbtList convertTextures(JsonArray jsonArray, List<Integer> textureGroupMappings) {
        //Stores which set at texture falls under
        HashMap<String, NbtCompound> setsByName = new HashMap<>();
        List<NbtCompound> setsByID = new ArrayList<>();

        //Foreach texture object
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                //Get texture Json object
                JsonObject textureObject = element.getAsJsonObject();

                //Get "source" field, which holds base64 encoded png data for the texture.
                String sourceData = textureObject.get("source").getAsString();
                sourceData = sourceData.replace("data:image/png;base64,", "");
                byte[] data = Base64.getDecoder().decode(sourceData);

                //Get name of texture.
                String name = textureObject.get("name").getAsString().replace(".png", "");


                //Split the name into the actual name, and the extension.
                String[] split = name.split("_");
                String groupName;
                String extension;

                if (split.length == 1) {
                    groupName = split[0];
                    extension = "";
                } else {
                    //Build string from all splits except the last
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < split.length - 1; i++)
                        builder.append(split[i]);

                    groupName = builder.toString(); //Assign built string as group name
                    extension = split[split.length - 1]; //Extension is last one
                }

                //If extension is invalid.
                if (!textureSetBuilders.containsKey(extension)) {
                    groupName = name;
                    extension = "";
                }

                //Compute the NBT Compound used for this set, if any.
                NbtCompound textureSet = setsByName.computeIfAbsent(groupName, (k) -> {
                    NbtCompound newCompound = new NbtCompound();
                    setsByID.add(newCompound);
                    return newCompound;
                });

                String textureTargetField = textureSetBuilders.get(extension);
                textureSet.put(textureTargetField, new NbtByteArray(data));
                int groupID = setsByID.indexOf(textureSet);
                textureGroupMappings.add(groupID);
            }
        }


        NbtList textureList = new NbtList();
        textureList.addAll(setsByID);
        return textureList;
    }

    public static final String[] faceNames = new String[]{
            "north",
            "south",
            "east",
            "west",
            "up",
            "down"
    };

    public static void convertCuboidElement(JsonObject source, NbtCompound target, List<Integer> textureGroupMappings) {
        transferFloatArray(source, target, "from");
        transferFloatArray(source, target, "to");
        transferInt(source, target, "inflate");

        //Transfer faces
        NbtList faceList = new NbtList();
        JsonObject faces = source.getAsJsonObject("faces");
        for (int i = 0; i < faceNames.length; i++) {
            String faceName = faceNames[i];

            JsonObject face = faces.getAsJsonObject(faceName);
            NbtCompound faceCompound = new NbtCompound();

            transferFloatArray(face, faceCompound, "uv");
            transferInt(face, faceCompound, "texture", textureGroupMappings);
            transferInt(face, faceCompound, "rotation");

            faceList.add(faceCompound);
        }

        target.put("faces", faceList);
        target.putInt("type", 1);
    }

    public static void convertMeshElement(JsonObject source, NbtCompound target, List<Integer> textureGroupMappings) {
        target.putInt("type", 2);
    }

    // -- Transfers --

    /**
     * Transfers commonly-used properties from the source to destination.
     * <p>
     * Includes transfers for
     * Name,
     * Origin, Rotation,
     * Color,
     * Locked, Visible,
     */
    private static void transferCommon(JsonObject source, NbtCompound target) {
        transferString(source, target, "name");

        NbtCompound transformation = new NbtCompound();
        transferFloatArray(source, transformation, "origin");
        transferFloatArray(source, transformation, "rotation");
        target.put("transform", transformation);

        transferBoolean(source, target, "visibility");
    }

    private static void transferString(JsonObject source, NbtCompound target, String key) {
        if (source.has(key)) target.putString(key, source.get(key).getAsString());
    }

    private static void transferFloatArray(JsonObject source, NbtCompound target, String key) {
        if (source.has(key)) {
            NbtList list = new NbtList();
            JsonArray array = source.get(key).getAsJsonArray();

            for (JsonElement element : array)
                list.add(NbtFloat.of(element.getAsFloat()));

            target.put(key, list);
        }
    }

    private static void transferBoolean(JsonObject source, NbtCompound target, String key) {
        if (source.has(key)) target.putBoolean(key, source.get(key).getAsBoolean());
    }

    private static void transferInt(JsonObject source, NbtCompound target, String key) {
        if (source.has(key)) {
            try {
                target.putInt(key, source.get(key).getAsInt());
            } catch (Exception e) {
                target.putInt(key, -1);
            }
        }
    }

    private static void transferInt(JsonObject source, NbtCompound target, String key, List<Integer> mapping) {
        if (source.has(key)) {
            try {
                int getSource = source.get(key).getAsInt();

                if(getSource >= 0 && getSource < mapping.size())
                    getSource = mapping.get(getSource);

                target.putInt(key, getSource);
            } catch (Exception e) {
                target.putInt(key, -1);
            }
        }
    }


    private static void transferFloat(JsonObject source, NbtCompound target, String key) {
        if (source.has(key)) target.putFloat(key, source.get(key).getAsFloat());
    }

}
