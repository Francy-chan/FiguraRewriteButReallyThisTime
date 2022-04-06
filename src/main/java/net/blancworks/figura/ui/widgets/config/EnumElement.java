package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.ContextMenu;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.utils.ColorUtils;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class EnumElement extends AbstractConfigElement {

    private final ArrayList<Text> names;
    private final TexturedButton button;
    private ContextMenu context;

    public EnumElement(int width, Config config, ConfigList parent) {
        super(width, config, parent);

        names = config.enumList;

        //toggle button
        button = new ParentedButton(0, 0, 90, 20, names.get((int) this.config.configValue % this.names.size()), this, button -> {
            this.context.setVisible(!this.context.isVisible());

            if (context.isVisible()) {
                updateContextText();
                UIHelper.setContext(this.context);
            }
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                //super
                super.renderButton(matrixStack, mouseX, mouseY, delta);

                //draw arrow
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                Text arrow = new LiteralText(context.isVisible() ? "^" : "V").setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
                textRenderer.drawWithShadow(
                        matrixStack, arrow.asOrderedText(),
                        this.x + this.width - textRenderer.getWidth(arrow) - 3, this.y + this.height / 2f - textRenderer.fontHeight / 2f,
                        !this.active ? Formatting.DARK_GRAY.getColorValue() : Formatting.WHITE.getColorValue()
                );
            }
        };
        children.add(0, button);

        //context menu
        context = new ContextMenu(button);
        for (int i = 0; i < names.size(); i++) {
            int finalI = i; //bruh
            context.addAction(names.get(i), button1 -> config.configValue = finalI);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        //reset enabled
        this.resetButton.active = this.config.configValue != this.config.defaultValue;

        //button text
        Text text = names.get((int) this.config.configValue % this.names.size());

        //edited colour
        if (this.config.configValue != this.initValue)
            text = text.copy().setStyle(ColorUtils.Colors.FRAN_PINK.style);

        //set text
        this.button.setMessage(text);

        //super render
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void setPos(int x, int y) {
        //update self pos
        super.setPos(x, y);

        //update button pos
        this.button.x = x + width - 154;
        this.button.y = y;

        //update context pos
        this.context.setPos(this.button.x + this.button.getWidth() / 2 - this.context.width / 2, this.button.y + 20);
    }

    private void updateContextText() {
        //cache entries
        List<ClickableWidget> entries = context.getEntries();

        //entries should have the same size as names
        //otherwise something went really wrong
        for (int i = 0; i < names.size(); i++) {
            //get text
            Text text = names.get(i);

            //selected entry
            if (i == (int) this.config.configValue % this.names.size())
                text = text.copy().setStyle(ColorUtils.Colors.FRAN_PINK.style);

            //apply text
            entries.get(i).setMessage(text);
        }
    }
}
