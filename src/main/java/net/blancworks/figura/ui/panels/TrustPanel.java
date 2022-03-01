package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.widgets.InteractableEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class TrustPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/trust.png");

    public TrustPanel() {
        super(new TranslatableText("figura.gui.panels.title.trust"));
    }

    @Override
    protected void init() {
        super.init();

        // -- right -- //

        int playerY = (int) (height * 0.25f);
        int entityWidth = (int) (width * 0.66);
        addDrawableChild(new InteractableEntity(width - entityWidth - 4, 32, entityWidth, height - 196, playerY, -15f, 30f, MinecraftClient.getInstance().player));
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
