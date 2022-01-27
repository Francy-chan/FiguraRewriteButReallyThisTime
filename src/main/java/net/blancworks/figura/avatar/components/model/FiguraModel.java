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
    /**
     * Holds all the model parts contained within this model
     */
    public final FiguraModelPart rootPart = new FiguraModelPart();

    public FiguraModel(FiguraAvatar owner) {
        super(owner);
    }


    /**
     * Renders the model!
     */
    public <T extends Entity> void render(FiguraRenderingState<T> renderingState) {
        rootPart.render(renderingState);
    }

    // -- IO --
    @Override
    public void readFromNBT(NbtCompound tag) {
        ImmutableMap.Builder<String, FiguraModelPart> builder = new ImmutableMap.Builder<>();
        for (String key : tag.getKeys()) {
            NbtCompound compound = tag.getCompound(key);
            FiguraModelPart part = FiguraModelPart.readPart(compound);

            builder.put(key, part);
        }
        rootPart.childParts = builder.build();
    }
}
