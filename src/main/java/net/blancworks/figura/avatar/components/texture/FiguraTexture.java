package net.blancworks.figura.avatar.components.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.FiguraNativeObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class FiguraTexture extends AbstractTexture {

    /**
     * The ID of the texture, used to register to Minecraft.
     */
    public final Identifier textureID = new Identifier("figura", "avatar_tex/" + UUID.randomUUID());

    /**
     * Native image holding the texture data for this texture.
     */
    private NativeImage nativeImage;

    /**
     * True if the texture is currently registered
     */
    private boolean isRegistered = false;

    public void readFromNBT(NbtByteArray tag) {
        try {
            //Get data from tag
            byte[] data = tag.getByteArray();

            //Put data in wrapper (for mojank reasons?)
            ByteBuffer wrapper = MemoryUtil.memAlloc(data.length);
            wrapper.put(data);
            wrapper.rewind();

            //Read image from wrapper
            nativeImage = NativeImage.read(wrapper);
        } catch (Exception e){

        }
    }

    @Override
    public void load(ResourceManager manager) throws IOException {
    }


    //Registers the texture to minecraft & uploads it, only if required.
    public void registerIfNeeded(FiguraAvatar avatar) {
        //Do nothing if already registered.
        if (isRegistered) return;
        isRegistered = true;

        //Register the texture to minecraft, and upload it to the GPU.
        registerAndUpload();

        //Add the texture to reload listeners in the texture manager.
        FiguraTextureManager.addTexture(this);

        //Add object as native object to the avatar.
        avatar.trackNativeObject(new FiguraTextureWrapper(textureID, getGlId(), nativeImage));
    }

    //Called when a texture is first created and when it reloads
    //Registers the texture to minecraft, and uploads it to GPU.
    public void registerAndUpload() {
        //Register texture under the ID, so Minecraft's rendering can use it.
        MinecraftClient.getInstance().getTextureManager().registerTexture(textureID, this);

        //Upload texture to GPU.
        TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
        nativeImage.upload(0, 0, 0, false);
    }


    public int getWidth(){
        return nativeImage.getWidth();
    }

    public int getHeight(){
        return nativeImage.getHeight();
    }


    /**
     * Holds the data for the figura texture, which will be dealllocated when the avatar is unloaded.
     */
    public static class FiguraTextureWrapper implements FiguraNativeObject {

        //Values to deallocate.
        public Identifier textureId;
        public NativeImage image;
        public int glid;

        public FiguraTextureWrapper(Identifier id, int glid, NativeImage image) {
            this.textureId = id;
            this.image = image;
            this.glid = glid;
        }

        @Override
        public void destroy() {

            //Remove texture from reload list
            FiguraTextureManager.removeTexture(textureId);

            //Close native image
            image.close();

            //Cache GLID and then release it on GPU
            int id = glid;
            RenderSystem.recordRenderCall(() -> {
                TextureUtil.releaseTextureId(id);
            });
        }
    }
}
