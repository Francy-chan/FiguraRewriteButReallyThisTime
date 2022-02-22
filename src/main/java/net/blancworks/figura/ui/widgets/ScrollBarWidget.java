package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ScrollBarWidget extends ClickableWidget implements Element, Selectable {

    // -- Variables -- //

    public boolean isScrolling = false;

    public int scrollBarHeight;

    public float scrollPixelPosition;

    // -- Constructors -- //

    public ScrollBarWidget(int x, int y, int width, int height, int scrollBarHeight) {
        super(x, y, width, height, new LiteralText(""));

        this.scrollBarHeight = scrollBarHeight;
    }

    // -- Functions -- //

    public void setHeight(float height){
        this.height = (int) height;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        isScrolling = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        isScrolling = false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);

        if (isScrolling) {
            scrollPixelPosition += deltaY / (float)height;
            scrollPixelPosition = MathHelper.clamp(scrollPixelPosition, 0, 1);
        }
    }

    public float getScrollProgress(){
        return scrollPixelPosition;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, new Identifier("figura", "textures/gui/scrollbar.png"));
        RenderSystem.enableDepthTest();

        drawTexture(matrices, x, y, width, 1, 10, 0, 10, 1, 20, 40);
        drawTexture(matrices, x, y + 1, width, height, 10, 1, 10, 18, 20, 40);
        drawTexture(matrices, x, y + 1 + height, width, 1, 10, 19, 10, 1, 20, 40);
        drawTexture(matrices, x, y + (int)MathHelper.lerp(scrollPixelPosition, 0, height - scrollBarHeight + 2), 0, 0, width, scrollBarHeight, 20, 40);

    }

    @Override
    public SelectionType getType() {
        return SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
