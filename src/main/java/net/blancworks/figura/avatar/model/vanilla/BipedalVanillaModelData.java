package net.blancworks.figura.avatar.model.vanilla;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class BipedalVanillaModelData<T extends BipedEntityModel<?>> extends VanillaModelData{
    protected T model;

    public BipedalVanillaModelData(Object targetObject) {
        super(targetObject);
        model = (T) targetObject;
    }


    @Override
    protected ModelPart getModelPart(String name) {
        return switch (name){
            case "head" -> model.head;
            case "body" -> model.body;
            case "leftarm" -> model.leftArm;
            case "rightarm" -> model.rightArm;
            case "leftleg" -> model.leftLeg;
            case "rightleg" -> model.rightLeg;

            default -> super.getModelPart(name);
        };
    }
}
