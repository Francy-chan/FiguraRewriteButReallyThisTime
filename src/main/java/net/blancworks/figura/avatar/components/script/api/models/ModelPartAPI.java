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

    @LuaWhitelist
    public Vec3f getPosition(){
        return target.transformation.position;
    }

    @Override
    public Object getFallback(String key) {
        return target.childParts.get(key);
    }
}
