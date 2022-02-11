package net.blancworks.figura.avatar.importing.importers;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.FiguraMod;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import org.apache.logging.log4j.util.TriConsumer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles importing .bbmodel files and converting them to an NBT tag for the avatar.
 * <p>
 * Also is responsible for pulling textures out of .bbmodels and using those as the textures for avatars.
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
    public List<Path> collectFiles(Path targetFolder) {
        try {
            return Files.walk(targetFolder).filter(p -> p.toString().toLowerCase().endsWith(".bbmodel")).collect(Collectors.toList());
        } catch (Exception e){
            FiguraMod.LOGGER.error(e);
        }

        return new ArrayList<>();
    }


    @Override
    public boolean importFiles(Path rootPath, List<Path> files, NbtCompound target) {
        boolean r = false;

        try {
            //Create target lists for models and textures to be put into.
            NbtList modelList = new NbtList();
            NbtList texturesList = new NbtList();

            //Import them one by one
            for (Path file : files)
                r |= importBBModelFile(rootPath, file, texturesList, modelList);

            //Put completed texture and model list into avatar data.
            target.put("textures", texturesList);
            target.put("models", modelList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return r;
    }



    // -- Helper Functions --

    public static boolean importBBModelFile(Path rootDir, Path filePath, NbtList textureList, NbtList modelList) throws Exception {
        String s = Files.readString(filePath);

        JsonObject rootObject = (JsonObject) jsonParser.parse(s);

        // -- Textures --
        //Create array of texture group mappings
        List<Integer> textureGroupMappings = new ArrayList<>();
        JsonArray textureArray = rootObject.getAsJsonArray("textures");

        //Convert textures and add them to texture list.
        convertAndAddTextures(textureArray, textureList, textureGroupMappings);

        // -- Model --
        //Sort elements and get outliner object
        ImmutableMap<UUID, JsonObject> elementsByUUID = sortElements(rootObject.getAsJsonArray("elements"));
        JsonArray outlinerArray = rootObject.getAsJsonArray("outliner");

        //Convert model to NBT.
        NbtCompound modelCompound = new NbtCompound();
        convertEntryList(outlinerArray, elementsByUUID, textureGroupMappings, modelCompound);

        //Construct properties for model
        NbtCompound properties = new NbtCompound();
        properties.putString("name", rootDir.relativize(filePath).toString().replace(".bbmodel", "").replace(File.separator, "/"));

        //Put properties in model
        modelCompound.put("properties", properties);

        //Add model to list of models
        modelList.add(modelCompound);

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


    public static NbtList convertAndAddTextures(JsonArray jsonArray, NbtList textureList, List<Integer> textureGroupMappings) {
        //Stores which set at texture falls under
        HashMap<String, NbtCompound> setsByName = new HashMap<>();
        List<NbtCompound> setsByID = new ArrayList<>();

        //Store how many texture groups we have already
        int startingID = textureList.size();

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

                //Finds and puts the texture binary data in right field of the texture set
                String textureTargetField = textureSetBuilders.get(extension);
                textureSet.put(textureTargetField, new NbtByteArray(data));

                //Add remapping for texture
                int groupID = setsByID.indexOf(textureSet) + startingID; //The final group ID of this texture takes into account texture groups that are already created
                textureGroupMappings.add(groupID);
            }
        }

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

                if (getSource >= 0 && getSource < mapping.size())
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
