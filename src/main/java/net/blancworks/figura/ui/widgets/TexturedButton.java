package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TexturedButton extends ButtonWidget {

    //texture data
    private Integer u;
    private Integer v;

    private final Integer textureWidth;
    private final Integer textureHeight;
    private final Integer interactionOffset;
    private final Identifier texture;

    //text data
    private final Text text;
    private Text tooltip;

    private boolean toggled = false;

    //texture and text constructor
    public TexturedButton(int x, int y, int width, int height, Integer u, Integer v, Integer interactionOffset, Identifier texture, Integer textureWidth, Integer textureHeight, Text text, Text tooltip, PressAction pressAction) {
        super(x, y, width, height, LiteralText.EMPTY, pressAction);

        this.u = u;
        this.v = v;
        this.interactionOffset = interactionOffset;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.text = text;
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
            this.hovered = UIHelper.isMouseOver(this, mouseX, mouseY);
            renderButton(matrixStack, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //render texture
        if (this.texture != null)
            renderTexture(matrixStack);
        else if (this.isHovered())
            UIHelper.fillRounded(matrixStack, this.x, this.y, this.width, this.height, 0x60FFFFFF);

        //render text
        if (this.text != null)
            renderText(matrixStack);

        //render tooltip
        if (this.tooltip != null && this.hovered)
            UIHelper.renderTooltip(matrixStack, this.tooltip, mouseX, mouseY);
    }

    private void renderTexture(MatrixStack matrixStack) {
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
        RenderSystem.setShaderTexture(0, this.texture);
        drawTexture(matrixStack, this.x, this.y, u, v, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    private void renderText(MatrixStack matrixStack) {
        //draw text
        drawCenteredTextWithShadow(
                matrixStack, MinecraftClient.getInstance().textRenderer,
                (this.toggled ? text.copy().formatted(Formatting.UNDERLINE) : text).asOrderedText(),
                this.x + this.width / 2, this.y + this.height / 2 - 4,
                (this.hovered || this.toggled ? Formatting.WHITE : Formatting.GRAY).getColorValue()
        );
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setUV(int x, int y) {
        this.u = x;
        this.v = y;
    }

    public void setTooltip(Text tooltip) {
        this.tooltip = tooltip;
    }
}
