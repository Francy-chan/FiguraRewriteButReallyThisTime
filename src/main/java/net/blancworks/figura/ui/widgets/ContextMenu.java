package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ContextMenu extends AbstractParentElement {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/context.png");

    private final List<ClickableWidget> entries = new ArrayList<>();
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

        int y = this.y + 1;
        boolean stripe = false;
        for (ClickableWidget entry : entries) {
            if (entry instanceof ContextDivisor)
                stripe = false;

            if (stripe) UIHelper.fill(matrices, x + 1, y, x + width - 1, y + entry.getHeight(), 0x22FFFFFF);
            y += entry.getHeight();
            stripe = !stripe;

            entry.render(matrices, mouseX, mouseY, delta);
        }

        matrices.pop();
    }

    public void addAction(Text name, ButtonWidget.PressAction action) {
        ContextButton button = new ContextButton(x, y + this.height, name, action);
        button.shouldHaveBackground(false);

        addElement(button);
    }

    public void addDivisor(Text name) {
        addElement(new ContextDivisor(x, y + this.height, name));
    }

    private void addElement(ClickableWidget element) {
        //add element
        children.add(element);
        entries.add(element);

        //update sizes
        this.width = Math.max(MinecraftClient.getInstance().textRenderer.getWidth(element.getMessage().asOrderedText()) + 8, width);
        this.height += element.getHeight();

        //fix buttons width
        for (ClickableWidget entry : entries)
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

        int heigth = y + 1;
        for (ClickableWidget button : entries) {
            button.x = x + 1;
            button.y = heigth;
            heigth += button.getHeight();
        }
    }

    public List<ClickableWidget> getEntries() {
        return entries;
    }

    public static class ContextButton extends TexturedButton {

        public ContextButton(int x, int y, Text text, PressAction pressAction) {
            super(x, y, 0, 16, text, null, pressAction);
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

    public static class ContextDivisor extends ClickableWidget {

        public ContextDivisor(int x, int y, Text message) {
            super(x, y, 0, 24, message);
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            //draw lines
            int y = this.y + this.height / 2 + textRenderer.fontHeight / 2 + 2;
            fill(matrices, this.x + 4, y, this.x + this.width - 8, y + 1, 0xFF000000 + Formatting.DARK_GRAY.getColorValue());

            //draw text
            textRenderer.drawWithShadow(
                    matrices, getMessage().asOrderedText(),
                    this.x + this.width / 2f - textRenderer.getWidth(getMessage()) / 2f, this.y + this.height / 2f - textRenderer.fontHeight / 2f - 1,
                    0xFFFFFF
            );
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return isMouseOver(mouseX, mouseY);
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }
    }
}
