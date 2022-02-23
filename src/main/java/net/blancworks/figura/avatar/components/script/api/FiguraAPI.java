package net.blancworks.figura.avatar.components.script.api;

import net.blancworks.figura.avatar.components.script.api.models.ModelPartAPI;
import net.blancworks.figura.avatar.components.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.newavatar.NewFiguraAvatar;

/**
 * Java-side object for global "figura"
 */
public class FiguraAPI extends ObjectWrapper<FiguraAPI> {

    // -- Variables -- //
    @LuaWhitelist
    public final ModelPartAPI models;

    // -- Constructors -- //
    public FiguraAPI(NewFiguraAvatar avatar){
        models = new ModelPartAPI();
        models.setTarget(avatar.getRoot());
    }

}
