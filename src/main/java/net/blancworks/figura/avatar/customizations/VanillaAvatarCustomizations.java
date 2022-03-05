package net.blancworks.figura.avatar.customizations;

import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class VanillaAvatarCustomizations implements FiguraCustomization<PlayerEntityModel<?>> {

    public final ModelPartCustomization headCustomization = new ModelPartCustomization();
    public final ModelPartCustomization bodyCustomization = new ModelPartCustomization();
    public final ModelPartCustomization leftArmCustomization = new ModelPartCustomization();
    public final ModelPartCustomization rightArmCustomization = new ModelPartCustomization();
    public final ModelPartCustomization leftLegCustomization = new ModelPartCustomization();
    public final ModelPartCustomization rightLegCustomization = new ModelPartCustomization();


    @Override
    public void apply(PlayerEntityModel<?> target) {
        headCustomization.apply(target.head);
        bodyCustomization.apply(target.body);
        leftArmCustomization.apply(target.leftArm);
        rightArmCustomization.apply(target.rightArm);
        leftLegCustomization.apply(target.leftLeg);
        rightLegCustomization.apply(target.rightLeg);
    }

    @Override
    public void revert(PlayerEntityModel<?> target) {
        headCustomization.revert(target.head);
        bodyCustomization.revert(target.body);
        leftArmCustomization.revert(target.leftArm);
        rightArmCustomization.revert(target.rightArm);
        leftLegCustomization.revert(target.leftLeg);
        rightLegCustomization.revert(target.rightLeg);
    }

    @Override
    public void clear() {
        headCustomization.clear();
        bodyCustomization.clear();
        leftArmCustomization.clear();
        rightArmCustomization.clear();
        leftLegCustomization.clear();
        rightLegCustomization.clear();
    }
}
