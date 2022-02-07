package net.blancworks.figura.avatar.components.script.api.models;

import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.reflector.wrappers.ObjectWrapper;
import net.minecraft.util.math.Vec3f;

/**
 * Wrapper class that encapsulates the accessing of a FiguraModelPart
 */
public class ModelPartAPI extends ObjectWrapper<FiguraModelPart> {

    // -- Functions -- //
    /**
     * Gets the Vec3f instance representing the position of this model part.
     */
    @LuaWhitelist
    public Vec3f getPosition(){
        return target.transformation.position;
    }

    /**
     * Gets the Vec3f instance representing the rotation of this model part.
     */
    @LuaWhitelist
    public Vec3f getRotation(){
        return target.transformation.rotation;
    }

    /**
     * Gets the Vec3f instance representing the origin of this model part.
     */
    @LuaWhitelist
    public Vec3f getOrigin(){
        return target.transformation.origin;
    }

    /**
     * Gets the Vec3f instance representing the scale of this model part.
     */
    @LuaWhitelist
    public Vec3f getScale(){
        return target.transformation.scale;
    }

    @Override
    public Object getFallback(String key) {
        return target.childParts.get(key);
    }
}
