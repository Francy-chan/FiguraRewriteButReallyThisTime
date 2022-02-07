package net.blancworks.figura.avatar.components.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.blancworks.figura.avatar.rendering.TransformData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Base for all model parts in Figura.
 * <p>
 * Should be data-only (aside from i/o). All elements should be public.
 * <p>
 * Model parts can contain parts as well.
 */
public class FiguraModelPart {

    public static final List<Supplier<FiguraModelPart>> partGenerators = new ImmutableList.Builder<Supplier<FiguraModelPart>>()
            .add(FiguraModelPart::new)
            .add(FiguraCuboidModelPart::new)
            .add(FiguraMeshModelPart::new)
            .build();

    /**
     * The type of part this is.
     */
    public int type = 0;

    /**
     * Transformation data, stores pos/rot/scale
     */
    public final TransformData transformation = new TransformData();

    /**
     * True if the part should render
     */
    public boolean shouldRender = true;

    /**
     * List of all parts contained within this part.
     */
    public Map<String, FiguraModelPart> childParts = new HashMap<>();

    // -- IO --
    public void readFromNBT(NbtCompound tag) {

        // -- Self Properties --
        type = tag.getInt("type");
        transformation.readFromNBT(tag.getCompound("transform"));

        // -- Child Parts --
        ImmutableMap.Builder<String, FiguraModelPart> builder = new ImmutableMap.Builder<>();
        NbtCompound childList = tag.getCompound("children");
        for (String key : childList.getKeys()) {
            //Get tag
            NbtCompound childTag = childList.getCompound(key);
            //Put in child set.
            builder.put(key, readPart(childTag));
        }
        childParts = builder.build();
    }

    public static FiguraModelPart readPart(NbtCompound tag) {
        //Get type from tag
        int cType = tag.getInt("type");
        //Generate a part from the type
        FiguraModelPart part = partGenerators.get(cType).get();
        //Read child from nbt tag
        part.readFromNBT(tag);
        return part;
    }


    /**
     * Draws a model part and its children using Minecraft's drawing system.
     */
    public <T extends Entity> void render(FiguraRenderingState<T> renderingState) {

        renderingState.poseStack.push();

        try {
            transformation.applyToStack(renderingState.poseStack);

            for (Map.Entry<String, FiguraModelPart> entry : childParts.entrySet()) {
                entry.getValue().render(renderingState);
            }
        } catch (Exception e){

        }

        renderingState.poseStack.pop();
    }


    // -- Nbt Reading Helper Functions --
    protected Vec3f vec3FromNbt(String key, NbtCompound compound) {
        if (!compound.contains(key, NbtElement.LIST_TYPE)) return new Vec3f();

        //Get float list
        //Always returns either an empty list or the actual target list.
        NbtList list = compound.getList(key, NbtElement.FLOAT_TYPE);

        //List isn't large enough, return 0
        // TODO - Change this to just return as many elements as are present in the list
        if (list.size() < 3) return new Vec3f();

        //Construct vec
        return new Vec3f(list.getFloat(0), list.getFloat(1), list.getFloat(2));
    }
    protected Vector4f vec4FromNbt(String key, NbtCompound compound) {
        if (!compound.contains(key, NbtElement.LIST_TYPE)) return new Vector4f();

        //Get float list
        //Always returns either an empty list or the actual target list.
        NbtList list = compound.getList(key, NbtElement.FLOAT_TYPE);

        //List isn't large enough, return 0
        // TODO - Change this to just return as many elements as are present in the list
        if (list.size() < 4) return new Vector4f();

        //Construct vec
        return new Vector4f(list.getFloat(0), list.getFloat(1), list.getFloat(2), list.getFloat(3));
    }

}
