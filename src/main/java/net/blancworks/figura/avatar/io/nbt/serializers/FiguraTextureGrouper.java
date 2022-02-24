package net.blancworks.figura.avatar.io.nbt.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.math.vector.FiguraVec2;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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

    private TextureSet getTextureSetByName(String modelName, String texName){
        return setsByTextureByModel.get(modelName).get(texName);
    }

    public int getTextureIndex(String modelName, String texName) {
        return getTextureSetByName(modelName, texName).index;
    }

    public int getTextureWidth(String modelName, String texName){
        return getTextureSetByName(modelName, texName).getWidth();
    }

    public int getTextureHeight(String modelName, String texName){
        return getTextureSetByName(modelName, texName).getHeight();
    }

    private static class TextureSet {

        private byte[] main;
        private byte[] emissive;
        private final int index;

        private TextureSet(int index) {
            this.index = index;
        }

        public void setMain(byte[] bytes) {
            if (emissive != null) {
                if (!checkSize(bytes, emissive))
                    FiguraMod.LOGGER.error("Emissive and main have different size!");
            }
            if (main != null)
                FiguraMod.LOGGER.error("Two textures with the same name!");
            main = bytes;
        }

        public void setEmissive(byte[] bytes) {
            if (main != null) {
                if (!checkSize(bytes, main))
                    FiguraMod.LOGGER.error("Emissive and main have different size!");
            }
            if (emissive != null)
                FiguraMod.LOGGER.error("Two textures with the same name!");
            emissive = bytes;
        }

        public int getWidth(){
            int w1 = (int)main[16] & 0xff;
            w1 = (w1 << 8) + ((int)main[17] & 0xff);
            w1 = (w1 << 8) + ((int)main[18] & 0xff);
            w1 = (w1 << 8) + ((int)main[19] & 0xff);

            return w1;
        }

        public int getHeight(){
            int h1 = ((int)main[20] & 0xff);
            h1 = (h1 << 8) + ((int)main[21] & 0xff);
            h1 = (h1 << 8) + ((int)main[22] & 0xff);
            h1 = (h1 << 8) + ((int)main[23] & 0xff);

            return h1;
        }

        //Checks that the image sizes of the PNGs match. Returns true if they do.
        private static boolean checkSize(byte[] png1, byte[] png2) {
            int w1 = png1[16];
            w1 = (w1 << 8) + png1[17];
            w1 = (w1 << 8) + png1[18];
            w1 = (w1 << 8) + png1[19];

            int h1 = png1[20];
            h1 = (h1 << 8) + png1[21];
            h1 = (h1 << 8) + png1[22];
            h1 = (h1 << 8) + png1[23];


            int w2 = png2[16];
            w2 = (w2 << 8) + png2[17];
            w2 = (w2 << 8) + png2[18];
            w2 = (w2 << 8) + png2[19];

            int h2 = png2[20];
            h2 = (h2 << 8) + png2[21];
            h2 = (h2 << 8) + png2[22];
            h2 = (h2 << 8) + png2[23];

            //System.out.println(w1 + " by " + h1 + " versus " + w2 + " by " + h2);

            return w1 == w2 && h1 == h2;
        }

    }

}
