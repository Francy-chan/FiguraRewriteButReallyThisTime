package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SliderWidget extends ScrollBarWidget {

    public static final Identifier SLIDER_TEXTURE = new Identifier("figura", "textures/gui/slider.png");

    protected static final int HEAD_SIZE = 11;
    private int steps = 0;
    private float stepSize = 0f;
    private float truePos = 0f;

    public SliderWidget(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height, initialValue);
        vertical = false;
    }

    public SliderWidget(int x, int y, int width, int height, float initialValue, int steps) {
        this(x, y, width, height, initialValue);

        steps = Math.max(steps, 1);
        this.steps = steps - 1;
        this.stepSize =  1f / (steps - 1);
    }

    @Override
    protected void scroll(double amount) {
        if (steps <= 0) {
            super.scroll(amount);
            return;
        }

        //set true pos
        truePos += amount / (float) ((vertical ? height - HEAD_HEIGHT : width - HEAD_WIDTH) + 2);
        truePos = MathHelper.clamp(truePos, 0, 1);

        //get closer steps
        float lowest = truePos - truePos % stepSize;
        float highest = lowest + stepSize;

        //get distance
        float distanceLow = Math.abs(lowest - truePos);
        float distanceHigh = Math.abs(highest - truePos);

        //apply rounded value
        scrollPixelPosition = distanceLow < distanceHigh ? lowest : highest;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

        //draw bar
        drawTexture(matrices, x, y + 3, width, 5, 0f, 0f, 5, 5, 22, 27);

        //draw steps
        if (steps > 0) {
            for (int i = 0; i < steps + 1; i++) {
                drawTexture(matrices, (int) (x + 3 + stepSize * i * (width - 11)), y + 3, 5, 5, 5f, 0f, 5, 5, 22, 27);
            }
        }

        //draw header
        drawTexture(matrices, x + (int) MathHelper.lerp(scrollPixelPosition, 0, width - HEAD_SIZE), y, hovered || isScrolling ? HEAD_SIZE : 0f, 5f + (vertical ? HEAD_SIZE : 0), HEAD_SIZE, HEAD_SIZE, 22, 27);
    }
}
