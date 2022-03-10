package net.blancworks.figura.avatar.customizations;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.serving.dealers.FiguraDealer;
import net.blancworks.figura.serving.entity.AvatarHolder;
import net.blancworks.figura.serving.entity.FiguraMetadata;

public class FiguraCustomizationManager {
    public final FiguraAvatar avatar;

    public final VanillaAvatarCustomizations vanillaAvatarCustomizations = new VanillaAvatarCustomizations();
    public final NameplateCustomizations nameplateCustomizations = new NameplateCustomizations();

    public FiguraCustomizationManager() {
        this(null);
    }

    public FiguraCustomizationManager(FiguraAvatar avatar) {
        this.avatar = avatar;
    }


    public void copyFromMetadata(FiguraMetadata metadata) {
        vanillaAvatarCustomizations.clear();

        for (int i = 0; i < FiguraDealer.MAX_AVATARS; i++) {
            for (AvatarHolder holder : metadata.entries) {
                if(holder == null || holder.entries[i] == null) continue;
                var avatar = holder.entries[i];

                if(vanillaAvatarCustomizations.headCustomization.visible == null)
                    vanillaAvatarCustomizations.headCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.headCustomization.visible;
                if(vanillaAvatarCustomizations.bodyCustomization.visible == null)
                    vanillaAvatarCustomizations.bodyCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.bodyCustomization.visible;
                if(vanillaAvatarCustomizations.leftArmCustomization.visible == null)
                    vanillaAvatarCustomizations.leftArmCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.leftArmCustomization.visible;
                if(vanillaAvatarCustomizations.rightArmCustomization.visible == null)
                    vanillaAvatarCustomizations.rightArmCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.rightArmCustomization.visible;
                if(vanillaAvatarCustomizations.leftLegCustomization.visible == null)
                    vanillaAvatarCustomizations.leftLegCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.leftLegCustomization.visible;
                if(vanillaAvatarCustomizations.rightLegCustomization.visible == null)
                    vanillaAvatarCustomizations.rightLegCustomization.visible = avatar.customizationManager.vanillaAvatarCustomizations.rightLegCustomization.visible;
            }
        }
    }
}
