package net.blancworks.figura.ui.panels;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SettingsPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/settings.png");

    public SettingsPanel() {
        super(new TranslatableText("figura.gui.panels.title.settings"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
