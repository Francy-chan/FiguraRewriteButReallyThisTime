package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.cards.CardEntity;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

public class WardrobePanel extends Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/wardrobe.png");

    public WardrobePanel() {
        super(new TranslatableText("figura.gui.panels.title.wardrobe"));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);

        matrices.push();
        matrices.translate(screen.x / 2f, screen.y / 2f, 0f);

        //player
        UIHelper.drawEntity(0, 0, 50, 0f, -45f, MinecraftClient.getInstance().player, matrices);

        //temp card
        matrices.push();
        matrices.translate(0f, 0f, 500f);

        CardEntity<PlayerEntity> card = new CardEntity<>(new Vec3f(0xFF / 255f, 0x72 / 255f, 0xB7 / 255f), 1, new LiteralText("Test"), new LiteralText("by Fran"), MinecraftClient.getInstance().player);
        card.setRotation(screen.x / 2f - mouseX, screen.y / 2f - mouseY);
        card.render(matrices, mouseX, mouseY, delta);

        matrices.pop();

        matrices.pop();
    }
}
