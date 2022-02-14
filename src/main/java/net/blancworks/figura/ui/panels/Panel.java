package net.blancworks.figura.ui.panels;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public interface Panel {
    default void init() {}
    void render(MatrixStack stack, int mouseX, int mouseY, float delta);
    Text getName();
}
