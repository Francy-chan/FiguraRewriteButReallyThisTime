package net.blancworks.figura.avatar.script.api.general;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.MapBackedWrapper;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.FiguraVec3;

public class NameplateAPI extends MapBackedWrapper<NameplateAPI, NameplateAPI.NameplatePartAPI> {

    public NameplateAPI(FiguraAvatar target) {
        NameplateCustomizations customizations = target.customizationManager.nameplateCustomizations;

        ImmutableMap.Builder<String, NameplatePartAPI> builder = new ImmutableMap.Builder<>();
        builder.put("chat", new NameplatePartAPI(customizations.chatNameplate));
        builder.put("entity", new NameplatePartAPI(customizations.entityNameplate));
        builder.put("list", new NameplatePartAPI(customizations.tablistNameplate));

        super.fallbackMap = builder.build();
    }

    public static class NameplatePartAPI extends ObjectWrapper<NameplatePartAPI> {

        NameplateCustomizations.NameplateCustomization customization;

        public NameplatePartAPI(NameplateCustomizations.NameplateCustomization customization) {
            this.customization = customization;
        }

        @LuaWhitelist
        public Boolean getEnabled() {
            return customization.enabled;
        }

        @LuaWhitelist
        public void setEnabled(Boolean enabled) {
            customization.enabled = enabled;
        }

        @LuaWhitelist
        public String getText() {
            return customization.text;
        }

        @LuaWhitelist
        public void setText(String text) {
            customization.text = text;
        }

        @LuaWhitelist
        public FiguraVec3 getPosition() {
            return customization.position;
        }

        @LuaWhitelist
        public void setPosition(FiguraVec3 position) {
            customization.position = position;
        }

        @LuaWhitelist
        public FiguraVec3 getScale() {
            return customization.scale;
        }

        @LuaWhitelist
        public void setScale(FiguraVec3 scale) {
            customization.scale = scale;
        }
    }
}
