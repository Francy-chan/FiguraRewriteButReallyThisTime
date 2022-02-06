package net.blancworks.figura.avatar.components.script.api.models;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.components.model.FiguraModelPart;
import net.blancworks.figura.avatar.components.script.reflector.wrappers.ObjectWrapper;

import java.util.Map;

/**
 * Wrapper class that encapsulates the accessing of a FiguraModelPart
 */
public class ModelPartAPI extends ObjectWrapper<ModelPartAPI> {
    // -- Variables -- //
    public FiguraModelPart targetPart;
    private final ImmutableMap<String, ModelPartAPI> children;

    // -- Constructors -- //
    public ModelPartAPI(FiguraModelPart targetPart) {
        this.targetPart = targetPart;


        ImmutableMap.Builder<String, ModelPartAPI> builder = ImmutableMap.builder();
        for (Map.Entry<String, FiguraModelPart> entry : targetPart.childParts.entrySet())
            builder.put(entry.getKey(), new ModelPartAPI(entry.getValue()));
        children = builder.build();
    }

    // -- Functions -- //

    @Override
    public Object getFallback(String key) {
        return super.getFallback(key);
    }
}
