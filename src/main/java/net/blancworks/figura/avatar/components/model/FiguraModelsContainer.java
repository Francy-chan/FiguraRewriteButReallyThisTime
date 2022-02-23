package net.blancworks.figura.avatar.components.model;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to manage the multi-model support of Figura
 */
public class FiguraModelsContainer extends FiguraAvatarComponent<NbtList> {
    // -- Variables -- //
    public final ArrayList<FiguraModel> modelsByIndex = new ArrayList<>();
    public final HashMap<String, FiguraModel> modelsByName = new HashMap<>();

    // -- Functions -- //

    /**
     * Simply renders each of the models contained within the container
     */
    public <T extends Entity> void render(FiguraRenderingState<T> renderingState) {
        //Render each model in the container
        for (FiguraModel model : modelsByIndex)
            model.render(renderingState);
    }

    @Override
    public void readFromNBT(NbtList tag) {
        //Only read compounds
        if(tag.getHeldType() != NbtElement.COMPOUND_TYPE) return;

        // For each compound, read a model out of it.
        for (int i = 0; i < tag.size(); i++) {
            FiguraModel newModel = new FiguraModel();
            newModel.readFromNBT(tag.getCompound(i));

            modelsByIndex.add(newModel);
            modelsByName.put(newModel.name, newModel);
        }
    }
}
