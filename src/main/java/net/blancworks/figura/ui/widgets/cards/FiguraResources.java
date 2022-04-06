package net.blancworks.figura.ui.widgets.cards;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FiguraResources implements SimpleSynchronousResourceReloadListener  {

    private static final Identifier RELOAD_LISTENER_ID = new Identifier("figura", "resource_reload_listener");

    @Override
    public Identifier getFabricId() {
        return RELOAD_LISTENER_ID;
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        loadCardBackgrounds(resourceManager);
    }

    public void loadCardBackgrounds(ResourceManager resourceManager) {
        //clear layers
        CardBackgroundElement.BackgroundType.BACKGROUND_DATA.clear();

        //load background data
        for (CardBackgroundElement.BackgroundType type : CardBackgroundElement.BackgroundType.values()) {
            try {
                //name
                String id = type.toString().toLowerCase();

                //load json
                JsonObject json = JsonParser.parseReader(new InputStreamReader(resourceManager.getResource(new Identifier("figura", "cards/" + id + ".json")).getInputStream())).getAsJsonObject();

                //get objects
                List<CardBackgroundElement.Layer> layers = new ArrayList<>();
                JsonArray textures = json.getAsJsonArray("textures");
                if (textures != null) {
                    int i = 0;
                    for (JsonElement jsonElement : textures) {//get texture
                        Identifier texture = new Identifier("figura", "textures/cards/" + id + "/layer_" + i + ".png");

                        //get animations
                        List<CardBackgroundElement.Animation> animations = new ArrayList<>();
                        JsonElement animationsJson = jsonElement.getAsJsonObject().get("animations");
                        if (animationsJson != null) {
                            for (JsonElement animation : animationsJson.getAsJsonArray()) {
                                JsonObject animationObj = animation.getAsJsonObject();
                                animations.add(new CardBackgroundElement.Animation(CardBackgroundElement.AnimationType.valueOf(animationObj.get("type").getAsString()), animationObj.get("speed").getAsFloat()));
                            }
                        }

                        //add layer to the list
                        layers.add(new CardBackgroundElement.Layer(texture, animations));
                        i++;
                    }
                }

                //set background data
                CardBackgroundElement.BackgroundType.BACKGROUND_DATA.put(type, layers);

            } catch (Exception e) {
                CardBackgroundElement.BackgroundType.BACKGROUND_DATA.put(type, List.of(CardBackgroundElement.MISSING));
                e.printStackTrace();
            }
        }
    }
}
