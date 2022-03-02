package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SliderWidget extends ScrollBarWidget {

    public static final Identifier SLIDER_TEXTURE = new Identifier("figura", "textures/gui/slider.png");

    protected static final int HEAD_SIZE = 11;

    public SliderWidget(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height, initialValue);
        vertical = false;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

        //draw bar
        drawTexture(matrices, x, y + 3, width, 5, 0f, 0f, 5, 5, 22, 27);

        //draw header
        drawTexture(matrices, x + (int) MathHelper.lerp(scrollPixelPosition, 0, width - HEAD_SIZE), y, hovered || isScrolling ? HEAD_SIZE : 0f, 5f + (vertical ? HEAD_SIZE : 0), HEAD_SIZE, HEAD_SIZE, 22, 27);
    }
}
