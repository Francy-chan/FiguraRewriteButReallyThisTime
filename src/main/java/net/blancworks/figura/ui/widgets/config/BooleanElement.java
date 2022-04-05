package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanElement extends AbstractConfigElement {

    private static final Text ON = new TranslatableText("figura.gui.on");
    private static final Text OFF = new TranslatableText("figura.gui.off");

    private final ParentedButton button;

    public BooleanElement(int width, Config config, ConfigList parent) {
        super(width, config, parent);

        //button
        button = new ParentedButton(0, 0, 90, 20, (boolean) config.configValue ? ON : OFF, this, button -> config.configValue = !(boolean) config.configValue);
        children.add(0, button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        //reset enabled
        this.resetButton.active = this.config.configValue != this.config.defaultValue;

        //button text
        Text text = (boolean) config.configValue ? ON : OFF;

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
        super.setPos(x, y);

        this.button.x = x + width - 154;
        this.button.y = y;
    }
}
