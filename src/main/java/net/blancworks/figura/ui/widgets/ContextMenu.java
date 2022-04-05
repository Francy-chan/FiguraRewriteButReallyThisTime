package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu extends AbstractParentElement {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/context.png");

    private final List<ContextButton> entries = new ArrayList<>();
    public final Element parent;

    public ContextMenu(Element parent) {
        super(0, 0, 0, 2);
        this.parent = parent;
        this.setVisible(false);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!isVisible()) return;

        //render
        matrices.push();
        matrices.translate(0f, 0f, 500f);

        UIHelper.renderSliced(matrices, x, y, width, height, BACKGROUND);
        for (int i = 0; i < entries.size(); i++) {
            if (i % 2 == 1)
                UIHelper.fill(matrices, x + 1, y + i * 16 + 1, x + width - 1, y + (i + 1) * 16 + 1, 0x22FFFFFF);

            entries.get(i).render(matrices, mouseX, mouseY, delta);
        }

        matrices.pop();
    }

    public void addAction(Text name, ButtonWidget.PressAction action) {
        //update sizes
        this.width = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(name.asOrderedText()) + 8, width);
        this.height += 16;

        //add children
        ContextButton button = new ContextButton(x, y + 16 * children().size(), 0, name, action);
        button.shouldHaveBackground(false);

        children.add(button);
        entries.add(button);

        //fix buttons width
        for (ContextButton entry : entries)
            entry.setWidth(this.width - 2);
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

        for (int i = 0; i < entries.size(); i++) {
            ContextButton button = entries.get(i);
            button.x = x + 1;
            button.y = y + 16 * i + 1;
        }
    }

    public List<ContextButton> getEntries() {
        return entries;
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
                    matrixStack, getMessage().asOrderedText(),
                    this.x + 3, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                    !this.active ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue()
            );
        }
    }
}
