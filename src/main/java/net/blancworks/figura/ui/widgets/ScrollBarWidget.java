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

    // -- Variables -- //

    public static final Identifier SCROLLBAR_TEXTURE = new Identifier("figura", "textures/gui/scrollbar.png");

    protected static final int HEAD_HEIGHT = 20;
    protected static final int HEAD_WIDTH = 10;
    protected boolean isScrolling = false;
    protected float scrollPixelPosition = 0f;
    protected boolean vertical = true;

    // -- Constructors -- //

    public ScrollBarWidget(int x, int y, int width, int height, float initialValue) {
        super(x, y, width, height, LiteralText.EMPTY);
        scroll(initialValue);
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
            float scrollPos = MathHelper.lerp(scrollPixelPosition, 0, (vertical ? height - HEAD_HEIGHT : width - HEAD_WIDTH) + 2);

            if (vertical && mouseY < y + scrollPos || mouseY > y + scrollPos + HEAD_HEIGHT)
                scroll(-(y + scrollPos + HEAD_HEIGHT / 2f - mouseY));
            else if (!vertical && mouseX < x + scrollPos || mouseX > x + scrollPos + HEAD_WIDTH)
                scroll(-(x + scrollPos + HEAD_WIDTH / 2f - mouseX));

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
        scroll(-amount * (vertical ? height : width) * 0.05);
        return true;
    }

    protected void scroll(double amount) {
        scrollPixelPosition += amount / (float) ((vertical ? height - HEAD_HEIGHT : width - HEAD_WIDTH) + 2);
        scrollPixelPosition = MathHelper.clamp(scrollPixelPosition, 0, 1);
    }

    public float getScrollProgress() {
        return scrollPixelPosition;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SCROLLBAR_TEXTURE);

        drawTexture(matrices, x, y, width, 1, 10, isScrolling ? 20 : 0, 10, 1, 20, 40);
        drawTexture(matrices, x, y + 1, width, height - 2, 10, isScrolling ? 21 : 1, 10, 18, 20, 40);
        drawTexture(matrices, x, y + height - 1, width, 1, 10, isScrolling ? 39 : 19, 10, 1, 20, 40);
        drawTexture(matrices, x, y + (int) MathHelper.lerp(scrollPixelPosition, 0, height - HEAD_HEIGHT), 0, hovered || isScrolling ? HEAD_HEIGHT : 0, HEAD_WIDTH, HEAD_HEIGHT, 20, 40);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
