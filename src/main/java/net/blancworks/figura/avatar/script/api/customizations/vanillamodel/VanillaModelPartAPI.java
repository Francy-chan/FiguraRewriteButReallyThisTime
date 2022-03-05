package net.blancworks.figura.avatar.script.api.customizations.vanillamodel;

import net.blancworks.figura.avatar.customizations.ModelPartCustomization;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;

public class VanillaModelPartAPI extends ObjectWrapper<VanillaModelPartAPI> {

    ModelPartCustomization customization;

    public VanillaModelPartAPI(ModelPartCustomization customization) {
        this.customization = customization;
    }

    @LuaWhitelist
    public void setVisible(Boolean bool) {
        customization.visible = bool;
    }
}
