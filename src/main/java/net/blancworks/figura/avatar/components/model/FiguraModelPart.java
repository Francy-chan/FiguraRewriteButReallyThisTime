package net.blancworks.figura.avatar.components.model;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import net.blancworks.figura.rendering.TransformData;
import net.minecraft.nbt.*;

import java.util.Map;

/**
 * Base for all model parts in Figura.
 *
 * Should be data-only (aside from i/o). All elements should be public.
 *
 * Model parts can contain parts as well.
 */
public class FiguraModelPart {
    /**
     * Transformation data, stores pos/rot/scale
     */
    public final TransformData transformation = new TransformData();

    /**
     * True if the part should render
     */
    public boolean shouldRender = true;

    /**
     * This is the raw vertex data for the model, stored directly as a float array. Generally will be faster to read
     * from because cache and stuff.
     */
    public final FloatArrayList rawVertexData = new FloatArrayList();

    /**
     * List of all parts contained within this part.
     */
    public Map<String, FiguraModelPart> childParts;

    // -- IO --
    public void readFromNBT(NbtCompound tag) {
        // -- Self Properties --
        transformation.readFromNBT(tag.getCompound("transform"));

        // -- Vertex Data --
        rawVertexData.clear();
        NbtList vertexDataList = new NbtList();
        for (int i = 0; i < vertexDataList.size(); i++)
            rawVertexData.add(vertexDataList.getFloat(i));

        // -- Child Parts --
        ImmutableMap.Builder<String, FiguraModelPart> builder = new ImmutableMap.Builder<>();
        NbtCompound childList = tag.getCompound("children");
        for (String key : childList.getKeys()) {
            NbtCompound childTag = childList.getCompound(key);
            FiguraModelPart part = new FiguraModelPart();
            part.readFromNBT(childTag);

            builder.put(key, part);
        }
        childParts = builder.build();

    }

    public void writeToNBT(NbtCompound tag) {
        // -- Self Properties --
        NbtCompound transformTag = new NbtCompound();
        transformation.writeToNBT(transformTag);
        tag.put("transform", transformTag);

        // -- Vertex Data --
        NbtList vertexData = new NbtList();
        for (Float vData : rawVertexData)
            vertexData.add(NbtFloat.of(vData));

        // -- Child Parts --
        NbtCompound childList = new NbtCompound();
        for (Map.Entry<String, FiguraModelPart> entry : childParts.entrySet()) {
            NbtCompound childNbt = new NbtCompound();
            entry.getValue().writeToNBT(childNbt);
            childList.put(entry.getKey(), childNbt);
        }
        tag.put("children", childList);
    }
}
