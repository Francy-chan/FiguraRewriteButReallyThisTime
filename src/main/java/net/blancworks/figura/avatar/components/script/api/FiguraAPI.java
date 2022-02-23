package net.blancworks.figura.avatar.components.script.api;

import net.blancworks.figura.avatar.components.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.newavatar.NewFiguraAvatar;
import net.blancworks.figura.avatar.newavatar.NewFiguraModelPart;

/**
 * Java-side object for global "figura"
 */
public class FiguraAPI extends ObjectWrapper<FiguraAPI> {

    // -- Variables -- //
    @LuaWhitelist
    public final NewFiguraModelPart models;

    // -- Constructors -- //
    public FiguraAPI(NewFiguraAvatar avatar){
        models = avatar.getRoot();
    }

}
