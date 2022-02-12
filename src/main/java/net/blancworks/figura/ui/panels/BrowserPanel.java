package net.blancworks.figura.ui.panels;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

public class BrowserPanel implements Panel {

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {

    }

    @Override
    public TranslatableText getName() {
        return new TranslatableText("figura.gui.panels.title.browser");
    }
}
