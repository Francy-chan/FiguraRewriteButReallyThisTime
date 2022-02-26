package net.blancworks.figura.avatar.model.vanilla;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL") // I shut up IntelliJ (unchecked casts, whatever, idc)
public class VanillaModelDataManager {
    // -- Variables -- //

    private static final Map<Object, VanillaModelData> vanillaModelDatas = new HashMap<>();
    private static final ImmutableMap<Object, ModelDataFactory> dataFactories = new ImmutableMap.Builder<Object, ModelDataFactory>()
            .put(PlayerEntityModel.class, PlayerVanillaModelData::new)
            .build();

    // -- Functions -- //

    public static VanillaModelData getModelData(EntityModel<?> model) {
        return vanillaModelDatas.computeIfAbsent(model, (c) -> dataFactories.getOrDefault(model.getClass(), VanillaModelData::new).run(model));
    }


    // -- Nested Types -- //
    @FunctionalInterface
    private static interface ModelDataFactory {
        VanillaModelData run(Object targetObject);
    }
}
