package net.blancworks.figura.avatar.components.script.api;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.script.api.math.matrix.MatricesAPI;
import net.blancworks.figura.avatar.components.script.api.math.vector.VectorsAPI;
import net.blancworks.figura.avatar.components.script.api.models.ModelsAPI;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

/**
 * Java-side object for global "figura"
 */
public class FiguraAPI extends ObjectWrapper<FiguraAPI> {

    // -- Variables -- //
    @LuaWhitelist
    public final ModelsAPI models;

    @LuaWhitelist
    public VectorsAPI vectors;
    @LuaWhitelist
    public MatricesAPI matrices;


    // -- Constructors -- //
    public FiguraAPI(FiguraAvatar avatar){
        models = new ModelsAPI(avatar.models);
        vectors = new VectorsAPI();
        matrices = new MatricesAPI();
    }

}
