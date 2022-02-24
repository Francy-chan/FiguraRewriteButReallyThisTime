package net.blancworks.figura.avatar.script.api;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

/**
 * Java-side object for global "figura"
 */
public class FiguraAPI extends ObjectWrapper<FiguraAPI> {

    // -- Variables -- //
    @LuaWhitelist
    public final FiguraModelPart models;

    // -- Constructors -- //
    public FiguraAPI(FiguraAvatar avatar){
        models = avatar.getRoot();
    }

}
