package net.blancworks.figura.avatar.model.vanilla;

import net.blancworks.figura.utils.TransformData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class PlayerVanillaModelData extends BipedalVanillaModelData<PlayerEntityModel<?>> {

    public PlayerVanillaModelData(Object targetObject) {
        super(targetObject);
    }

    @Override
    protected void transferData(ModelPart source, TransformData destination) {
        destination.origin.set(-source.pivotX, 24-source.pivotY, source.pivotZ);
        destination.rotation.set(-source.pitch, -source.yaw, source.roll);
    }

}
