package net.blancworks.figura.avatar.components.script.api.models;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.reflector.custom.FallbackAPI;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

/**
 * Wrapper class that encapsulates the accessing of a FiguraModelPart
 */
public class ModelPartAPI implements FallbackAPI {

    // -- Variables -- //
    private final FiguraModelPart targetPart; //The part this model API instance targets
    private final ImmutableMap<String, ModelPartAPI> children; //The child objects of this API instance

    // -- Constructors -- //

    public ModelPartAPI(FiguraModelPart part) {
        targetPart = part;

        //Create API objects for children of the target part
        ImmutableMap.Builder<String, ModelPartAPI> builder = new ImmutableMap.Builder<>();
        for (Map.Entry<String, FiguraModelPart> entry : targetPart.childParts.entrySet())
            builder.put(entry.getKey(), new ModelPartAPI(entry.getValue()));
        children = builder.build();
    }

    // -- Functions -- //

    @LuaWhitelist
    public void setRotation(float x, float y, float z) {
        targetPart.transformation.rotation = new Vec3f(x, y, z);
    }

    private float[] rotationCache = new float[3];

    @LuaWhitelist
    public float[] getRotation() {
        rotationCache[0] = targetPart.transformation.rotation.getX();
        rotationCache[1] = targetPart.transformation.rotation.getY();
        rotationCache[2] = targetPart.transformation.rotation.getZ();

        return rotationCache;
    }


    // - Fallback - //
    @Override
    public Object getFallback(String key) {
        return children.get(key);
    }
}
