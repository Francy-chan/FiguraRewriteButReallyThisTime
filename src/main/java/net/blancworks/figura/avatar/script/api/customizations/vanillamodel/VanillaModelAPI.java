package net.blancworks.figura.avatar.script.api.customizations.vanillamodel;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.customizations.VanillaAvatarCustomizations;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.MapBackedWrapper;

public class VanillaModelAPI extends MapBackedWrapper<VanillaModelAPI, VanillaModelPartAPI> {

    public VanillaModelAPI(FiguraAvatar target){
        VanillaAvatarCustomizations customizations = target.customizationManager.vanillaAvatarCustomizations;

        var builder = new ImmutableMap.Builder<String, VanillaModelPartAPI>();
        builder.put("head", new VanillaModelPartAPI(customizations.headCustomization));
        builder.put("body", new VanillaModelPartAPI(customizations.bodyCustomization));
        builder.put("leftArm", new VanillaModelPartAPI(customizations.leftArmCustomization));
        builder.put("rightArm", new VanillaModelPartAPI(customizations.rightArmCustomization));
        builder.put("leftLeg", new VanillaModelPartAPI(customizations.leftLegCustomization));
        builder.put("rightLeg", new VanillaModelPartAPI(customizations.rightLegCustomization));

        super.fallbackMap = builder.build();
    }
}
