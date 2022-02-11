package net.blancworks.figura.ui.gui;

import net.blancworks.figura.ui.cards.CardEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;

public class FiguraWardrobeScreen extends Screen {

    private final Screen parentScreen;

    public FiguraWardrobeScreen(Screen parent) {
        super(new LiteralText("FiguraWardrobeScreen"));
        this.parentScreen = parent;
    }

    @Override
    public void onClose() {
        this.client.setScreen(parentScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        CardEntity<PlayerEntity> card = new CardEntity<>(new LiteralText("Test"), new LiteralText("test2"), 0xFF72B7, 1, MinecraftClient.getInstance().player);

        matrices.push();
        matrices.translate(MinecraftClient.getInstance().getWindow().getScaledWidth() / 2f, MinecraftClient.getInstance().getWindow().getScaledHeight() / 2f, 0f);
        card.render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }
}
