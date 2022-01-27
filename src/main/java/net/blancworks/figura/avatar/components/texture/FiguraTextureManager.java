package net.blancworks.figura.avatar.components.texture;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class FiguraTextureManager {

    private static final HashMap<Identifier,FiguraTexture> reloadTextures = new HashMap<>();


    public static void init(){
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("figura", "textures");
            }

            @Override
            public synchronized void reload(ResourceManager manager) {
                for (Map.Entry<Identifier, FiguraTexture> entry : reloadTextures.entrySet()) {
                    entry.getValue().registerAndUpload();
                }
            }
        });
    }


    public synchronized static void addTexture(FiguraTexture texture){
        reloadTextures.put(texture.textureID, texture);
    }

    public synchronized static void removeTexture(Identifier textureID){
        reloadTextures.remove(textureID);
    }

}
