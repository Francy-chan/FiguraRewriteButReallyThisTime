package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu extends AbstractParentElement implements Drawable, FiguraDrawable, Selectable {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/context.png");

    public int x = 0;
    public int y = 0;
    public int width = 0;
    public int height = 2;

    private final List<ContextButton> children = new ArrayList<>();
    private boolean visible = true;

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //render
        matrices.push();
        matrices.translate(0f, 0f, 500f);

        UIHelper.renderSliced(matrices, x, y, width, height, BACKGROUND);
        for (int i = 0; i < children.size(); i++) {
            if (i % 2 == 1)
                UIHelper.fill(matrices, x + 1, y + i * 16 + 1, x + width - 1, y + (i + 1) * 16 + 1, 0x22FFFFFF);

            children.get(i).render(matrices, mouseX, mouseY, delta);
        }

        matrices.pop();
    }

    public void addAction(Text name, ButtonWidget.PressAction action) {
        //update sizes
        this.width = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(name.asOrderedText()) + 8, width);
        this.height += 16;

        //add children
        children.add(new ContextButton(x, y + 16 * children().size(), 0, name, action));

        //fix buttons width
        for (ContextButton button : children)
            button.setWidth(this.width - 2);
    }

    public void setPos(int x, int y) {
        //fix out of screen
        int realWidth = x + width;
        int clientWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        if (realWidth > clientWidth)
            x -= (realWidth - clientWidth);

        int realHeight = y + height;
        int clientHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();
        if (realHeight > clientHeight)
            y -= (realHeight - clientHeight);

        //apply changes
        this.x = x;
        this.y = y;

        for (int i = 0; i < children().size(); i++) {
            ContextButton button = (ContextButton) this.children().get(i);
            button.x = x + 1;
            button.y = y + 16 * i + 1;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return this.children;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public static class ContextButton extends TexturedButton {

        public ContextButton(int x, int y, int width, Text text, PressAction pressAction) {
            super(x, y, width, 16, text, null, pressAction);
        }

        @Override
        protected void renderText(MatrixStack matrixStack) {
            //draw text
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.drawWithShadow(
                    matrixStack, text.asOrderedText(),
                    this.x + 3, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                    !this.active ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue()
            );
        }
    }
}
