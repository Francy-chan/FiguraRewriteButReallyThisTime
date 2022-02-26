package net.blancworks.figura.avatar.model.vanilla;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.utils.TransformData;
import net.minecraft.client.model.ModelPart;

import java.lang.reflect.Field;
import java.util.Map;

public class VanillaModelData {

    // -- Variables -- //
    private final Map<String, ModelPart> parts;
    private final TransformData data = new TransformData();


    public VanillaModelData(Object targetObject) {
        ImmutableMap.Builder<String, ModelPart> builder = ImmutableMap.builder();
        addModelParts(targetObject, targetObject.getClass(), builder);
        this.parts = builder.build();
    }

    private static void addModelParts(Object targetObject, Class<?> targetClass, ImmutableMap.Builder<String, ModelPart> builder) {
        if (targetClass == Object.class)
            return;

        for (Field declaredField : targetClass.getDeclaredFields()) {
            Class<?> fieldClass = declaredField.getType();

            if (fieldClass == ModelPart.class) {
                declaredField.setAccessible(true);

                try {
                    builder.put(declaredField.getName(), (ModelPart) declaredField.get(targetObject));
                } catch (Exception e) {
                    FiguraMod.LOGGER.error(e);
                }

                declaredField.setAccessible(false);
            }
        }

        addModelParts(targetObject, targetClass.getSuperclass(), builder);
    }


    protected void transferData(ModelPart source, TransformData destination) {
        destination.position.set(source.pivotX, source.pivotY, source.pivotZ);
        destination.rotation.set(source.pitch, source.yaw, source.roll);
    }

    protected ModelPart getModelPart(String name) {
        return parts.get(name);
    }

    public TransformData getData(String name) {
        ModelPart part = getModelPart(name);

        if (part == null) {
            return null;
        } else {
            transferData(part, data);

            data.rotation.set(
                    Math.toDegrees(data.rotation.x),
                    Math.toDegrees(data.rotation.y),
                    Math.toDegrees(data.rotation.z)
            );
        }

        return data;
    }
}
