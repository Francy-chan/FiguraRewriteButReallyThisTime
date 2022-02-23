package net.blancworks.figura.avatar.components.script.api.models;

import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.newavatar.NewFiguraModelPart;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.math.vector.FiguraVec3;

/**
 * Wrapper class that encapsulates the accessing of a FiguraModelPart
 */
public class ModelPartAPI extends ObjectWrapper<NewFiguraModelPart> {

    // -- Functions -- //
    /**
     * Gets the LuaVec3 instance representing the position of this model part.
     */
    @LuaWhitelist
    public FiguraVec3 getPosition(){
        return target.transform.position;
    }

    @LuaWhitelist
    public void setPosition(double x, double y, double z) {
        target.transform.position.x = x;
        target.transform.position.y = y;
        target.transform.position.z = z;
        target.transform.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setPosition(FiguraVec3 vec) {
        setPosition(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the LuaVec3 instance representing the rotation of this model part.
     */
    @LuaWhitelist
    public FiguraVec3 getRotation(){
        return target.transform.rotation;
    }

    @LuaWhitelist
    public void setRotation(double x, double y, double z) {
        target.transform.rotation.x = x;
        target.transform.rotation.y = y;
        target.transform.rotation.z = z;
        target.transform.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setRotation(FiguraVec3 vec) {
        setRotation(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the LuaVec3 instance representing the origin of this model part.
     */
    @LuaWhitelist
    public FiguraVec3 getOrigin(){
        return target.transform.origin;
    }

    @LuaWhitelist
    public void setOrigin(double x, double y, double z) {
        target.transform.origin.x = x;
        target.transform.origin.y = y;
        target.transform.origin.z = z;
        target.transform.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setOrigin(FiguraVec3 vec) {
        setOrigin(vec.x, vec.y, vec.z);
    }

    /**
     * Gets the LuaVec3 instance representing the scale of this model part.
     */
    @LuaWhitelist
    public FiguraVec3 getScale(){
        return target.transform.scale;
    }

    @LuaWhitelist
    public void setScale(double x, double y, double z) {
        target.transform.scale.x = x;
        target.transform.scale.y = y;
        target.transform.scale.z = z;
        target.transform.needsMatrixRecalculation = true;
    }

    @LuaWhitelist
    public void setScale(FiguraVec3 vec) {
        setScale(vec.x, vec.y, vec.z);
    }

    @LuaWhitelist
    public void setMatrix(FiguraMat4 mat4) {
        target.transform.positionMatrix.copyFrom(mat4);
    }

    @Override
    public Object getFallback(String key) {
        return target.getChild(key);
    }
}
