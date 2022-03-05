package net.blancworks.figura.avatar.customizations;

import net.minecraft.client.model.ModelPart;

public class ModelPartCustomization implements FiguraCustomization<ModelPart> {
    public Boolean visible;
    private boolean prevVisible;

    @Override
    public void apply(ModelPart target) {
        prevVisible = target.visible;

        if(visible != null)
            target.visible = visible;
    }

    @Override
    public void revert(ModelPart target) {
        target.visible = prevVisible;
    }

    @Override
    public void clear() {
        visible = null;
    }
}
