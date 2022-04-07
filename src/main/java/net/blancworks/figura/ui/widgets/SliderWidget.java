package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SliderWidget extends ScrollBarWidget {

    // -- fields -- //

    public static final Identifier SLIDER_TEXTURE = new Identifier("figura", "textures/gui/slider.png");

    protected final int headHeight = 11;
    protected final int headWidth = 11;

    private boolean isStepped = false;
    private float stepSize = 0f;
    private int steps = 0;

    private float steppedPos;

    // -- constructors -- //

    public SliderWidget(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height, initialValue);
        vertical = false;
        steppedPos = initialValue;
    }

    public SliderWidget(int x, int y, int width, int height, float initialValue, int steps) {
        this(x, y, width, height, initialValue);
        setSteps(steps);
    }

    // -- methods -- //

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.active) return false;
        if (!isStepped) return super.mouseScrolled(mouseX, mouseY, amount);

        scroll(stepSize * Math.signum(-amount) * (width - headWidth + 2f));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.active) return false;

        if (isStepped && keyCode > 261 && keyCode < 266) {
            scroll(stepSize * (keyCode % 2 == 0 ? 1 : -1) * (width - headWidth + 2f));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void scroll(double amount) {
        //normal scroll
        super.scroll(amount);
        if (!isStepped) return;

        //get the closest step
        steppedPos = getClosestStep();
    }

    private float getClosestStep() {
        //get closer steps
        float lowest = scrollPrecise - scrollPrecise % stepSize;
        float highest = lowest + stepSize;

        //get distance
        float distanceLow = Math.abs(lowest - scrollPrecise);
        float distanceHigh = Math.abs(highest - scrollPrecise);

        //return closest
        return distanceLow < distanceHigh ? lowest : highest;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            //set hovered
            this.hovered = this.isMouseOver(mouseX, mouseY);

            //render hovered background
            if (this.active && this.isHovered())
                UIHelper.fillRounded(matrices, x, y, width, height, 0x60FFFFFF);

            //render button
            this.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SLIDER_TEXTURE);

        //draw bar
        drawTexture(matrices, x, y + 3, width, 5, isScrolling ? 10f : 0f, 0f, 5, 5, 33, 16);

        //draw steps
        if (isStepped) {
            for (int i = 0; i < steps; i++) {
                drawTexture(matrices, (int) Math.floor(x + 3 + stepSize * i * (width - 11)), y + 3, 5, 5, isScrolling ? 15f : 5f, 0f, 5, 5, 33, 16);
            }
        }

        //draw header
        lerpPos(delta);
        drawTexture(matrices, x + Math.round(MathHelper.lerp(scrollPos, 0, width - headWidth)), y, active ? (isHovered() || isScrolling ? headWidth * 2 : headWidth) : 0f, 5f, headWidth, headHeight, 33, 16);
    }

    // -- getters and setters -- //

    @Override
    public float getScrollProgress() {
        return isStepped ? steppedPos : super.getScrollProgress();
    }

    @Override
    public void setScrollProgress(float amount, boolean force) {
        if (isStepped) {
            steppedPos = force ? amount : MathHelper.clamp(amount, 0f, 1f);
        }

        super.setScrollProgress(amount, force);
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        //set steps data
        this.isStepped = steps > 1;
        this.steps = steps;
        this.stepSize = 1f / (steps - 1);

        //update scroll
        scroll(0f);
    }

    public int getStepValue() {
        return Math.round(getScrollProgress() * (getSteps() - 1));
    }
}
