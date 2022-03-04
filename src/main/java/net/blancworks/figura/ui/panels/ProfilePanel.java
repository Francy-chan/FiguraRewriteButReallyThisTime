package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ProfilePanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/profile.png");

    public ProfilePanel() {
        super(new TranslatableText("figura.gui.panels.title.profile").formatted(Formatting.RED));
    }

    @Override
    public void init() {
        FiguraToast.sendToast("not yet!", "<3");
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
