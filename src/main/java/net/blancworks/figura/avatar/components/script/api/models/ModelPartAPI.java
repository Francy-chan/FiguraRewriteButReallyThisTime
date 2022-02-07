package net.blancworks.figura.avatar.components.script.api.models;

import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
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

    @LuaWhitelist
    public void setPosition(float x, float y, float z) { target.transformation.position.set(x,y,z); }

    /**
     * Gets the Vec3f instance representing the rotation of this model part.
     */
    @LuaWhitelist
    public Vec3f getRotation(){
        return target.transformation.rotation;
    }

    @LuaWhitelist
    public void setRotation(float x, float y, float z) { target.transformation.rotation.set(x,y,z); }

    /**
     * Gets the Vec3f instance representing the origin of this model part.
     */
    @LuaWhitelist
    public Vec3f getOrigin(){
        return target.transformation.origin;
    }

    @LuaWhitelist
    public void setOrigin(float x, float y, float z) { target.transformation.origin.set(x,y,z); }

    /**
     * Gets the Vec3f instance representing the scale of this model part.
     */
    @LuaWhitelist
    public Vec3f getScale(){
        return target.transformation.scale;
    }

    @LuaWhitelist
    public void setScale(float x, float y, float z) { target.transformation.scale.set(x,y,z); }

    @Override
    public Object getFallback(String key) {
        return target.childParts.get(key);
    }
}
