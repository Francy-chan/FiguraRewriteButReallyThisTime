package net.blancworks.figura.avatar.components.script.api.models;

import net.blancworks.figura.avatar.components.model.FiguraModel;
import net.blancworks.figura.avatar.components.model.FiguraModelsContainer;
import net.blancworks.figura.avatar.components.script.lua.reflector.wrappers.ObjectWrapper;

/**
 * Java-side object for global "figura.models"
 */
public class ModelsAPI extends ObjectWrapper<ModelsAPI> {
    // -- Variables -- //
    public FiguraModelsContainer container;

    // -- Constructors -- //
    public ModelsAPI(FiguraModelsContainer container) {
        this.container = container;
    }

    // -- Functions -- //
    @Override
    public Object getFallback(String key) {
        FiguraModel model = container.modelsByName.get(key);
        return model == null ? null : model.rootPart;
    }
}
