package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.AbstractParentElement;
import net.blancworks.figura.ui.widgets.SwitchButton;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class ConfigWidget extends AbstractParentElement {

    protected final List<AbstractConfigElement> entries = new ArrayList<>();
    private final ConfigList parent;
    private ContainerButton parentConfig;

    public ConfigWidget(int width, Text name, Text tooltip, ConfigList parent) {
        super(0, 0, width, 20);
        this.parent = parent;

        this.parentConfig = new ContainerButton(parent, x, y, width, 20, name, tooltip, button -> {
            setShowChildren(this.parentConfig.isToggled());
            parent.updateScroll();
        });

        this.parentConfig.setToggled(true);
        this.parentConfig.shouldHaveBackground(false);
        children.add(this.parentConfig);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //children background
        if (parentConfig.isToggled() && entries.size() > 0)
            UIHelper.fill(matrices, x, y + 21, x + width, y + height, 0x11FFFFFF);

        //children
        super.render(matrices, mouseX, mouseY, delta);
    }

    public void addConfig(Config config) {
        AbstractConfigElement element = switch (config.type) {
            case BOOLEAN -> new BooleanElement(width, config, parent);
            case ENUM -> new EnumElement(width, config, parent);
            case INPUT -> new InputElement(width, config, parent);
            case KEYBIND -> new KeybindElement(width, config, parent);
            default -> null;
        };

        if (element == null)
            return;

        this.height += 22;
        this.children.add(element);
        this.entries.add(element);
    }

    public int getHeight() {
        return parentConfig.isToggled() ? height : 20;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;

        this.parentConfig.x = x;
        this.parentConfig.y = y;

        for (int i = 0; i < entries.size(); i++)
            entries.get(i).setPos(x, y + 22 * (i + 1));
    }

    public void setShowChildren(boolean bool) {
        this.parentConfig.setToggled(bool);
        for (AbstractConfigElement element : entries)
            element.setVisible(bool);
    }

    public boolean isShowingChildren() {
        return parentConfig.isToggled();
    }

    public static class ContainerButton extends SwitchButton {

        private final ConfigList parent;
        private final Text tooltip;

        public ContainerButton(ConfigList parent, int x, int y, int width, int height, Text text, Text tooltip, PressAction pressAction) {
            super(x, y, width, height, text, null, pressAction);
            this.parent = parent;
            this.tooltip = tooltip;
        }

        @Override
        protected void renderText(MatrixStack matrixStack) {
            //get text color
            int color = !this.active || !this.isToggled() ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue();

            //draw text
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.drawWithShadow(
                    matrixStack, getMessage().asOrderedText(),
                    this.x + 3, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                    color
            );

            //draw arrow
            Text arrow = new LiteralText(this.toggled ? "^" : "V").setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
            textRenderer.drawWithShadow(
                    matrixStack, arrow.asOrderedText(),
                    this.x + this.width - textRenderer.getWidth(arrow) - 3, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                    color
            );
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            boolean over = this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);

            if (over) {
                parent.hoverText = tooltip;
            } else if (parent.hoverText == tooltip)
                parent.hoverText = null;

            return over;
        }
    }
}
