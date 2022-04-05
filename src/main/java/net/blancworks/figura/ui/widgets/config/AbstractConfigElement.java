package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.widgets.AbstractParentElement;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public abstract class AbstractConfigElement extends AbstractParentElement {

    protected final Config config;
    protected final ConfigList parent;

    protected TexturedButton resetButton;

    protected Object initValue;

    private boolean hovered = false;

    public AbstractConfigElement(int width, Config config, ConfigList parent) {
        super(0, 0, width, 20);
        this.config = config;
        this.parent = parent;
        this.initValue = config.value;

        //reset button
        resetButton = new ParentedButton(x + width - 60, y, 60, 20, new TranslatableText("controls.reset"), this, button -> config.configValue = config.defaultValue);
        children.add(resetButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;
        hovered = isMouseOver(mouseX, mouseY);

        //render name
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrices, config.name, x + 16, y + height / 2f - textRenderer.fontHeight / 2f, 0xFFFFFF);

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        boolean over = this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);

        if (over && mouseX < this.x + this.width - 180) {
            parent.hoverText = config.tooltip;
        } else if (parent.hoverText == config.tooltip)
            parent.hoverText = null;

        return over;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;

        resetButton.x = x + width - 60;
        resetButton.y = y;
    }

    public boolean isHovered() {
        return hovered;
    }

    protected static class ParentedButton extends TexturedButton {

        private final AbstractConfigElement parent;

        public ParentedButton(int x, int y, int width, int height, Text text, AbstractConfigElement parent, PressAction pressAction) {
            super(x, y, width, height, text, null, pressAction);
            this.parent = parent;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isHovered() && super.isMouseOver(mouseX, mouseY);
        }
    }
}
