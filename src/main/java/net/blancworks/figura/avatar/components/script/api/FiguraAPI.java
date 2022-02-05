package net.blancworks.figura.avatar.components.script.api;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.script.api.models.ModelsAPI;
import net.blancworks.figura.avatar.components.script.reflector.LuaWhitelist;

/**
 * Java-side object for global "figura"
 */
public class FiguraAPI {

    // -- Variables -- //
    @LuaWhitelist
    public final ModelsAPI models;


    // -- Constructors -- //
    public FiguraAPI(FiguraAvatar avatar){
        models = new ModelsAPI(avatar);
    }
}
