package net.blancworks.figura.ui.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.cards.CardEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

public class FiguraWardrobeScreen extends Screen {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background.png");
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
        RenderSystem.setShaderTexture(0, BACKGROUND);
        renderBackgroundTexture(0);

        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());

        CardEntity<PlayerEntity> card = new CardEntity<>(new Vec3f(0xFF / 255f, 0x72 / 255f, 0xB7 / 255f), new LiteralText("Test"), new LiteralText("by Fran"), MinecraftClient.getInstance().player);
        card.setRotation(screen.x / 2f - mouseX, screen.y / 2f -mouseY);

        matrices.push();
        matrices.translate(screen.x / 2f, screen.y / 2f, 500f);
        card.render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override
    public void renderBackgroundTexture(int vOffset) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0f, this.height, 0f).texture(0f, this.height / 32f + vOffset).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(this.width, this.height, 0f).texture(this.width / 32f, this.height / 32f + vOffset).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(this.width, 0f, 0f).texture(this.width / 32f, vOffset).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(0f, 0f, 0f).texture(0f, vOffset).color(255, 255, 255, 255).next();
        tessellator.draw();
    }
}
