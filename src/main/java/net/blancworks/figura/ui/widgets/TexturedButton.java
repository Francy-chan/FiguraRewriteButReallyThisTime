package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class TexturedButton extends ButtonWidget {

    //texture data
    private final Integer u;
    private final Integer v;
    private final Integer textureWidth;
    private final Integer textureHeight;
    private final Integer interactionOffset;
    private final Identifier texture;

    //text data
    private final MutableText text;

    private boolean disabled = false;
    private boolean selected = false;

    //texture and text constructor
    public TexturedButton(int x, int y, int width, int height, Integer u, Integer v, Integer interactionOffset, Identifier texture, Integer textureWidth, Integer textureHeight, MutableText text, PressAction pressAction) {
        super(x, y, width, height, LiteralText.EMPTY, pressAction);

        this.u = u;
        this.v = v;
        this.interactionOffset = interactionOffset;
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.text = text;
    }

    //text constructor
    public TexturedButton(int x, int y, int width, int height, MutableText text, PressAction pressAction) {
        this(x, y, width, height, null, null, null, null, null, null, text, pressAction);
    }

    //texture constructor
    public TexturedButton(int x, int y, int width, int height, int u, int v, int interactionOffset, Identifier texture, int textureWidth, int textureHeight, PressAction pressAction) {
        this(x, y, width, height, u, v, interactionOffset, texture, textureWidth, textureHeight, null, pressAction);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            renderButton(matrixStack, mouseX, mouseY, delta);
        }
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //render texture
        if (this.texture != null)
            renderTexture(matrixStack);

        //render text
        if (this.text != null)
            renderText(matrixStack);
    }

    private void renderTexture(MatrixStack matrixStack) {
        //prepare render
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);

        //uv transforms
        int u = this.u;
        int v = this.v;
        if (this.isHovered())
            v += this.interactionOffset;
        if (this.isDisabled())
            u -= this.interactionOffset;

        //draw texture
        drawTexture(matrixStack, this.x, this.y, u, v, this.width, this.height, this.textureWidth, this.textureHeight);
    }

    private void renderText(MatrixStack matrixStack) {
        //draw text
        drawCenteredTextWithShadow(
                matrixStack, MinecraftClient.getInstance().textRenderer,
                (this.selected ? text.copy().formatted(Formatting.UNDERLINE) : text).asOrderedText(),
                this.x + this.width / 2, this.y + this.height / 2 - 4,
                (this.hovered | this.selected ? Formatting.WHITE : Formatting.GRAY).getColorValue()
        );
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
