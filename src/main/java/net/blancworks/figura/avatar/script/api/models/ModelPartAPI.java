package net.blancworks.figura.avatar.script.api.models;

import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.math.vector.FiguraVec3;

/**
 * Wrapper class that encapsulates the accessing of a FiguraModelPart
 */
public class ModelPartAPI extends ObjectWrapper<FiguraModelPart> {

    // -- Functions -- //
    /**
     * Gets the Vec3 instance representing the position of this model part.
     * NOTE - This returns a REFERENCE to the position. Modifying this value will modify the position.
     */
    @LuaWhitelist
    public FiguraVec3 getPosition(){
        return target.getTransform().position;
    }

    /**
     * Sets the position of this model part.
     * @param x The new X coordinate to set.
     * @param y The new Y coordinate to set.
     * @param z The new Z coordinate to set.
     */
    @LuaWhitelist
    public void setPosition(double x, double y, double z) {
        target.getTransform().position.set(x, y, z);
        target.getTransform().needsMatrixRecalculation = true;
    }

    /**
     * Sets the position of this model part.
     * @param vec A Vec3 to copy the position values from.
     */
    @LuaWhitelist
    public void setPosition(FiguraVec3 vec) {
        setPosition(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3 instance representing the rotation of this model part.
     * NOTE - This returns a REFERENCE to the rotation. Modifying this value will modify the rotation.
     */
    @LuaWhitelist
    public FiguraVec3 getRotation(){
        return target.getTransform().rotation;
    }

    /**
     * Sets the rotation of this model part.
     * @param x The new X angle to set.
     * @param y The new Y angle to set.
     * @param z The new Z angle to set.
     */
    @LuaWhitelist
    public void setRotation(double x, double y, double z) {
        target.getTransform().rotation.set(x, y, z);
        target.getTransform().needsMatrixRecalculation = true;
    }

    /**
     * Sets the rotation of this model part
     * @param vec A Vec3 to copy the rotation values from
     */
    @LuaWhitelist
    public void setRotation(FiguraVec3 vec) {
        setRotation(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3 instance representing the origin of this model part.
     * NOTE - This returns a REFERENCE to the origin. Modifying this value will modify the origin.
     */
    @LuaWhitelist
    public FiguraVec3 getOrigin(){
        return target.getTransform().origin;
    }

    /**
     * Sets the origin of this model part.
     * @param x The new X origin to set.
     * @param y The new Y origin to set.
     * @param z The new Z origin to set.
     */
    @LuaWhitelist
    public void setOrigin(double x, double y, double z) {
        target.getTransform().origin.set(x, y, z);
        target.getTransform().needsMatrixRecalculation = true;
    }

    /***
     * Sets the origin of this model part.
     * @param vec A Vec3 to copy the rotation values from
     */
    @LuaWhitelist
    public void setOrigin(FiguraVec3 vec) {
        setOrigin(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the Vec3 instance representing the scale of this model part.
     */
    @LuaWhitelist
    public FiguraVec3 getScale(){
        return target.getTransform().scale;
    }

    /**
     * Sets the scale of this model part.
     * @param x The new X scale to set.
     * @param y The new Y scale to set.
     * @param z The new Z scale to set.
     */
    @LuaWhitelist
    public void setScale(double x, double y, double z) {
        target.getTransform().scale.set(x, y, z);
        target.getTransform().needsMatrixRecalculation = true;
    }

    /**
     * Sets the scale of this model part.
     * @param vec A Vec3 to copy the scale values from
     */
    @LuaWhitelist
    public void setScale(FiguraVec3 vec) {
        setScale(vec.x, vec.y, vec.z);
    }

    /**
     * Sets the matrix of this model part, overriding the normal transformations.
     * @param mat4 The matrix to set for the model part.
     */
    @LuaWhitelist
    public void setMatrix(FiguraMat4 mat4) {
        target.getTransform().positionMatrix.copyFrom(mat4);
    }

    /**
     * Flags the matrix to be re-calculated next frame.
     * Generally only needs to be used if you're directly modifying the transformation values themselves instead of using the set functions.
     */
    @LuaWhitelist
    public void regenerateMatrix(){

    }

    /**
     * Returns the child object of this model part.
     * @param key The name of the child.
     * @return The child, or nil if none was found.
     */
    @Override
    public Object getFallback(String key) {
        return target.getChild(key);
    }
}
