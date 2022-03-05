package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ScrollBarWidget extends ClickableWidget {

    // -- fields -- //

    public static final Identifier SCROLLBAR_TEXTURE = new Identifier("figura", "textures/gui/scrollbar.png");

    protected final int headHeight = 20;
    protected final int headWidth = 10;

    protected boolean isScrolling = false;
    protected boolean vertical = true;

    protected float scrollPos;
    protected float scrollPrecise;

    protected PressAction action;

    // -- constructors -- //

    public ScrollBarWidget(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height, LiteralText.EMPTY);
        scrollPrecise = initialValue;
        scrollPos = initialValue;
    }

    // -- methods -- //

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        if (button == 0) {
            //jump to pos when not clicking on head
            float scrollPos = MathHelper.lerp(scrollPrecise, 0f, (vertical ? height - headHeight : width - headWidth) + 2f);

            if (vertical && mouseY < y + scrollPos || mouseY > y + scrollPos + headHeight)
                scroll(-(y + scrollPos + headHeight / 2f - mouseY));
            else if (!vertical && mouseX < x + scrollPos || mouseX > x + scrollPos + headWidth)
                scroll(-(x + scrollPos + headWidth / 2f - mouseX));

            isScrolling = true;
            playDownSound(MinecraftClient.getInstance().getSoundManager());
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && isScrolling) {
            isScrolling = false;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isScrolling) {
            //vertical drag
            if (vertical && mouseY >= this.y && mouseY <= this.y + this.height) {
                scroll(deltaY);
                return true;
            }
            //horizontal drag
            else if (!vertical && mouseX >= this.x && mouseX <= this.x + this.width) {
                scroll(deltaX);
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll(-amount * (vertical ? height : width) * 0.05f);
        return true;
    }

    //apply scroll value
    protected void scroll(double amount) {
        scrollPrecise += amount / ((vertical ? height - headHeight : width - headWidth) + 2f);
        setScrollProgress(scrollPrecise);
    }

    //animate scroll head
    protected void lerpPos(float delta) {
        scrollPos = (float) MathHelper.lerp(1 - Math.pow(0.2f, delta), scrollPos, getScrollProgress());
    }

    //render the scroll
    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SCROLLBAR_TEXTURE);

        //render bar
        drawTexture(matrices, x, y, width, 1, 10f, isScrolling ? 20f : 0f, 10, 1, 20, 40);
        drawTexture(matrices, x, y + 1, width, height - 2, 10f, isScrolling ? 21f : 1f, 10, 18, 20, 40);
        drawTexture(matrices, x, y + height - 1, width, 1, 10f, isScrolling ? 39f : 19f, 10, 1, 20, 40);

        //render head
        lerpPos(delta);
        drawTexture(matrices, x, y + (int) MathHelper.lerp(scrollPos, 0, height - headHeight), 0f, hovered || isScrolling ? headHeight : 0f, headWidth, headHeight, 20, 40);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    // -- getters and setters -- //

    //set scrollbar height
    public void setHeight(int height) {
        this.height = height;
    }

    //get scroll value
    public float getScrollProgress() {
        return scrollPrecise;
    }

    //manually set scroll
    public void setScrollProgress(float amount) {
        setScrollProgress(amount, false);
    }

    //manually set scroll with optional clamping
    public void setScrollProgress(float amount, boolean force) {
        scrollPrecise = force ? amount : MathHelper.clamp(amount, 0f, 1f);

        if (action != null)
            action.onPress(this);
    }

    //set button action
    public void setAction(PressAction action) {
        this.action = action;
    }

    //press action
    public interface PressAction {
        void onPress(ScrollBarWidget scrollbar);
    }
}
