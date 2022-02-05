package net.blancworks.figura.avatar.components.script.api.models;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.script.reflector.custom.FallbackAPI;

/**
 * Java-side object for global "figura.models"
 */
public class ModelsAPI implements FallbackAPI {
    // -- Variables -- //
    private ImmutableMap<String, ModelPartAPI> modelList;

    // -- Constructors -- //
    public ModelsAPI(FiguraAvatar avatar){

        //TODO - Multi-model support
        ImmutableMap.Builder<String, ModelPartAPI> builder = new ImmutableMap.Builder<>();
        builder.put("root", new ModelPartAPI(avatar.model.rootPart));
        modelList = builder.build();
    }

    // -- Functions -- //
    @Override
    public Object getFallback(String key) {
        return modelList.get(key);
    }
}
