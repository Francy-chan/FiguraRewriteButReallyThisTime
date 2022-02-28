package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ScrollBarWidget extends ClickableWidget implements Element, Selectable {

    // -- Variables -- //

    public static final Identifier SCROLLBAR_TEXTURE = new Identifier("figura", "textures/gui/scrollbar.png");
    private static final int SCROLL_HEAD_HEIGHT = 20;
    private boolean isScrolling = false;
    private float scrollPixelPosition;

    // -- Constructors -- //

    public ScrollBarWidget(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);
    }

    // -- Functions -- //

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        if (button == 0) {
            //jump to pos when not clicking on head
            float scrollPos = MathHelper.lerp(scrollPixelPosition, 0, height - SCROLL_HEAD_HEIGHT + 2);
            if (mouseY < y + scrollPos || mouseY > y + scrollPos + SCROLL_HEAD_HEIGHT)
                scroll(-(y + scrollPos + SCROLL_HEAD_HEIGHT / 2f - mouseY));

            isScrolling = true;
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
        if (isScrolling && (mouseY >= this.y && mouseY <= this.y + this.height)) {
            scroll(deltaY);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scroll(-amount * height * 0.05);
        return true;
    }

    private void scroll(double amount) {
        scrollPixelPosition += amount / (float) (height - SCROLL_HEAD_HEIGHT + 2);
        scrollPixelPosition = MathHelper.clamp(scrollPixelPosition, 0, 1);
    }

    public float getScrollProgress() {
        return scrollPixelPosition;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SCROLLBAR_TEXTURE);

        drawTexture(matrices, x, y, width, 1, 10, 0, 10, 1, 20, 40);
        drawTexture(matrices, x, y + 1, width, height - 2, 10, 1, 10, 18, 20, 40);
        drawTexture(matrices, x, y + height - 1, width, 1, 10, 19, 10, 1, 20, 40);
        drawTexture(matrices, x, y + (int) MathHelper.lerp(scrollPixelPosition, 0, height - SCROLL_HEAD_HEIGHT), 0, this.hovered || this.isScrolling ? 20 : 0, width, SCROLL_HEAD_HEIGHT, 20, 40);
    }

    @Override
    public SelectionType getType() {
        return SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
