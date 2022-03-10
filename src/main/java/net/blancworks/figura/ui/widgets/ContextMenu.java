package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ContextMenu extends Panel implements Element {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/context.png");

    private boolean visible = false;

    public ContextMenu() {
        super(0, 0, 0, 0, LiteralText.EMPTY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        //fix out of screen
        int realWidth = x + width;
        int clientWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        if (realWidth > clientWidth)
            this.setPos(x - (realWidth - clientWidth), y);

        //render
        matrices.push();
        matrices.translate(0f, 0f, 500f);

        UIHelper.renderSliced(matrices, x, y, width, height, BACKGROUND);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.pop();
    }

    public void addAction(Text name, ButtonWidget.PressAction action) {
        //update sizes
        this.width = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(name.asOrderedText()) + 3, width);
        this.height += 15;

        //add children
        this.addDrawableChild(new ContextButton(x, y + 15 * children().size(), this.width, name, action));
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;

        for (int i = 0; i < children().size(); i++) {
            ContextButton button = (ContextButton) this.children().get(i);
            button.x = x;
            button.y = y + 15 * i;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;

        for (Element element : this.children()) {
            if (element instanceof ClickableWidget widget)
                widget.visible = visible;
        }
    }

    public static class ContextButton extends TexturedButton {

        public ContextButton(int x, int y, int width, Text text, PressAction pressAction) {
            super(x, y, width, 15, text, null, pressAction);
        }

        @Override
        protected void renderText(MatrixStack matrixStack) {
            //draw text
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(
                    matrixStack, text.asOrderedText(),
                    this.x + 2, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                    !this.active ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue()
            );
        }
    }
}
