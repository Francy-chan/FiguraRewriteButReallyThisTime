package net.blancworks.figura.avatar.components.model;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

public class FiguraModel extends FiguraAvatarComponent<NbtCompound> {

    // -- Variables -- //
    /**
     * Holds all the model parts contained within this model
     */
    public final FiguraModelPart rootPart = new FiguraModelPart();

    /**
     * The name of the model.
     */
    public String name;

    // -- Constructors -- //

    public FiguraModel(FiguraAvatar owner) {
        super(owner);
    }

    // -- Functions -- //

    /**
     * Renders the model!
     */
    public <T extends Entity> void render(FiguraRenderingState<T> renderingState) {
        rootPart.render(renderingState);
    }

    // - IO - //
    @Override
    public void readFromNBT(NbtCompound tag) {
        //Get child objects
        ImmutableMap.Builder<String, FiguraModelPart> builder = new ImmutableMap.Builder<>();
        for (String key : tag.getKeys()) {
            if (key.equals("properties")) continue; // Skip properties tag.

            NbtCompound compound = tag.getCompound(key);
            FiguraModelPart part = FiguraModelPart.readPart(compound);

            builder.put(key, part);
        }
        rootPart.childParts = builder.build();

        // - Get Properties - //
        NbtCompound properties = tag.getCompound("properties");
        name = properties.getString("name");
    }
}
