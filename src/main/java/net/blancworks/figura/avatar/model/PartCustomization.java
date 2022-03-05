package net.blancworks.figura.avatar.model;

import net.blancworks.figura.utils.TransformData;
import net.blancworks.figura.utils.math.vector.FiguraVec4;
import org.jetbrains.annotations.Nullable;

//Could potentially have this and TransformData use a pool like the math objects do?
public class PartCustomization {

    public final TransformData transformData;
    public final FiguraVec4 color;
    @Nullable
    public String renderMode;

    public PartCustomization() {
        transformData = new TransformData();
        color = FiguraVec4.get(1, 1, 1, 1);
    }
}
