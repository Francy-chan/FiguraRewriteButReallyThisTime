package net.blancworks.figura.ui.panels;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public interface Panel {
    void render(MatrixStack stack, int mouseX, int mouseY, float delta);
    TranslatableText getName();
}
