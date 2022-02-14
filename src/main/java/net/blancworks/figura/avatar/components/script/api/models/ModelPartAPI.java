package net.blancworks.figura.avatar.components.script.api.models;

import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.api.math.matrix.LuaMatrix4;
import net.blancworks.figura.avatar.components.script.api.math.vector.LuaVec3;
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
    public LuaVec3 getPosition(){
        return target.transformation.position;
    }

    @LuaWhitelist
    public void setPosition(double x, double y, double z) {
        target.transformation.position.x = x;
        target.transformation.position.y = y;
        target.transformation.position.z = z;
        target.transformation.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setPosition(LuaVec3 vec) {
        setPosition(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3f instance representing the rotation of this model part.
     */
    @LuaWhitelist
    public LuaVec3 getRotation(){
        return target.transformation.rotation;
    }

    @LuaWhitelist
    public void setRotation(double x, double y, double z) {
        target.transformation.rotation.x = x;
        target.transformation.rotation.y = y;
        target.transformation.rotation.z = z;
        target.transformation.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setRotation(LuaVec3 vec) {
        setRotation(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3f instance representing the origin of this model part.
     */
    @LuaWhitelist
    public LuaVec3 getOrigin(){
        return target.transformation.origin;
    }

    @LuaWhitelist
    public void setOrigin(double x, double y, double z) {
        target.transformation.origin.x = x;
        target.transformation.origin.y = y;
        target.transformation.origin.z = z;
        target.transformation.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setOrigin(LuaVec3 vec) {
        setOrigin(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3f instance representing the scale of this model part.
     */
    @LuaWhitelist
    public LuaVec3 getScale(){
        return target.transformation.scale;
    }

    @LuaWhitelist
    public void setScale(double x, double y, double z) {
        target.transformation.scale.x = x;
        target.transformation.scale.y = y;
        target.transformation.scale.z = z;
        target.transformation.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setScale(LuaVec3 vec) {
        setScale(vec.x, vec.y, vec.z);
    }

    @LuaWhitelist
    public void setMatrix(LuaMatrix4 mat4) {
        target.transformation.positionMatrix.copyFrom(mat4);
    }

    @Override
    public Object getFallback(String key) {
        return target.childParts.get(key);
    }
}
