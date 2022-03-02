package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.widgets.InteractableEntity;
import net.blancworks.figura.ui.widgets.PlayerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class TrustPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/trust.png");

    PlayerList playerList;
    InteractableEntity entityWidget;

    public TrustPanel() {
        super(new TranslatableText("figura.gui.panels.title.trust"));
    }

    @Override
    protected void init() {
        super.init();

        // -- left -- //

        //player list
        playerList = new PlayerList(12, 32, 220, height - 44); // 174 entry + 32 padding + 10 slider + 4 slider padding
        addDrawableChild(playerList);

        // -- right -- //

        //entity widget
        int playerY = (int) (height * 0.25f);
        entityWidget = new InteractableEntity(240, 32, width - 252, height / 2, playerY, -15f, 30f, MinecraftClient.getInstance().player);
        addDrawableChild(entityWidget);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //set entity to render
        PlayerList.PlayerEntry entity = playerList.getSelectedEntry();
        if (entity != null) {
            PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(entity.getId());
            entityWidget.setEntity(player);
        }

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
