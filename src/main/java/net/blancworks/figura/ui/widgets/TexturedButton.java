package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TexturedButton extends ButtonWidget {

    //texture data
    protected Integer u;
    protected Integer v;

    protected final Integer textureWidth;
    protected final Integer textureHeight;
    protected final Integer interactionOffset;
    protected final Identifier texture;

    //extra fields
    protected Text tooltip;
    private boolean hasBackground = true;

    //texture and text constructor
    public TexturedButton(int x, int y, int width, int height, Integer u, Integer v, Integer interactionOffset, Identifier texture, Integer textureWidth, Integer textureHeight, Text text, Text tooltip, PressAction pressAction) {
        super(x, y, width, height, text, pressAction);

        this.u = u;
        this.v = v;
        this.interactionOffset = interactionOffset;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.tooltip = tooltip;
    }

    //text constructor
    public TexturedButton(int x, int y, int width, int height, Text text, Text tooltip, PressAction pressAction) {
        this(x, y, width, height, null, null, null, null, null, null, text, tooltip, pressAction);
    }

    //texture constructor
    public TexturedButton(int x, int y, int width, int height, int u, int v, int interactionOffset, Identifier texture, int textureWidth, int textureHeight, Text tooltip, PressAction pressAction) {
        this(x, y, width, height, u, v, interactionOffset, texture, textureWidth, textureHeight, null, tooltip, pressAction);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            //set hovered
            this.hovered = this.isMouseOver(mouseX, mouseY);

            //render hovered background
             if (this.active && this.isHovered())
                UIHelper.fillRounded(matrixStack, x, y, width, height, 0x60FFFFFF);

             //render button
            this.renderButton(matrixStack, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //render texture
        if (this.texture != null)
            renderTexture(matrixStack, delta);
        else if (this.hasBackground)
            UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);

        //render text
        if (this.getMessage() != null)
            renderText(matrixStack);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isHovered() && this.isMouseOver(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        boolean over = UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY);
        if (over) UIHelper.setTooltip(this.tooltip);
        return over;
    }

    protected void renderTexture(MatrixStack matrixStack, float delta) {
        //uv transforms
        int u = this.u;
        int v = this.v;
        if (this.isHovered())
            v += this.interactionOffset;
        if (!this.active)
            u -= this.interactionOffset;

        //draw texture
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);

        int size = this.interactionOffset;
        drawTexture(matrixStack, this.x + this.width / 2 - size / 2, this.y + this.height / 2 - size / 2, u, v, size, size, this.textureWidth, this.textureHeight);
    }

    protected void renderText(MatrixStack matrixStack) {
        //draw text
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        drawCenteredTextWithShadow(
                matrixStack, textRenderer,
                this.getMessage().asOrderedText(),
                this.x + this.width / 2, this.y + this.height / 2 - textRenderer.fontHeight / 2,
                !this.active ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue()
        );
    }

    public void setUV(int x, int y) {
        this.u = x;
        this.v = y;
    }

    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }

    public void shouldHaveBackground(boolean bool) {
        this.hasBackground = bool;
    }
}
