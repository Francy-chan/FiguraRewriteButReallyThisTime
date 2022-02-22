package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.CardList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class WardrobePanel extends Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");

    public CardList cardList;

    public WardrobePanel() {
        super(new TranslatableText("figura.gui.panels.title.wardrobe"));
    }

    @Override
    protected void init() {
        super.init();

        cardList = new CardList(32, height - 64, width - 64, height - 64 - 4);
        addDrawableChild(cardList);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);

        matrices.push();
        matrices.translate(screen.x / 2f, screen.y / 2f, 0f);

        //player
        UIHelper.drawEntity(0, 0, 50, 0f, 45f, MinecraftClient.getInstance().player, matrices);


        matrices.pop();

        super.render(matrices, mouseX, mouseY, delta);
    }
}
