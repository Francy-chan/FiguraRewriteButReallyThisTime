package net.blancworks.figura.utils;

import net.blancworks.figura.avatar.model.vanilla.VanillaModelData;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;

public class RenderingUtils {

    //Used by card rendering
    public static FiguraEntityMetadata overrideMetadata;

    //Used in general
    public static VanillaModelData vanillaModelData;

    public static FiguraEntityMetadata<?> currentEntityMetadata;
}
