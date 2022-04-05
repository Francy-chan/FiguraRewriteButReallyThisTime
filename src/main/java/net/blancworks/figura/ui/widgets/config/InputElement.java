package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.TextField;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.function.Consumer;

public class InputElement extends AbstractConfigElement {

    private final TextField textField;
    private final boolean isHex;

    public InputElement(int width, Config config, ConfigList parent) {
        super(width, config, parent);

        //get input type
        Config.InputType inputType = config.inputType;
        isHex = inputType == Config.InputType.HEX_COLOR;

        //text field
        textField = new InputField(0, 0, 90, 20, inputType.hint, this, text -> {
            //only write config value if it's valid
            if (inputType.validator.test(text))
                config.configValue = isHex ? ColorUtils.rgbToInt(ColorUtils.hexStringToRGB(text)) : text;
        });
        updateTextFieldText(formatText(config.configValue));
        textField.getField().setCursorToStart();

        children.add(0, textField);

        //overwrite reset button to update the text field
        children.remove(resetButton);
        resetButton = new ParentedButton(x + width - 60, y, 60, 20, new TranslatableText("controls.reset"), this, button -> {
            config.configValue = config.defaultValue;
            updateTextFieldText(formatText(config.configValue));
        });
        children.add(resetButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        String text = textField.getField().getText();

        //reset enabled
        this.resetButton.active = !text.equals(formatText(config.defaultValue));

        //text colour
        int color = 0xFFFFFF;

        //invalid config
        if (!config.inputType.validator.test(text)) {
            color = 0xFF5555;
        }
        //config was changed
        else if (!text.equals(formatText(initValue))) {
            color = ColorUtils.Colors.FRAN_PINK.hex;
        }

        //set text colour
        textField.getField().setEditableColor(color);
        textField.setBorderColour(0xFF000000 + color);

        //super render
        super.render(matrices, mouseX, mouseY, delta);

        //hex colour preview
        if (isHex) {
            //border
            UIHelper.fillRounded(matrices, x + width - 178, y, 20, 20, getTextField().getField().isFocused() ? 0xFFFFFFFF : 0xFF404040);
            //inside
            UIHelper.fillRounded(matrices, x + width - 177, y + 1, 18, 18, 0xFF000000 + (int) config.configValue);
        }
    }

    @Override
    public void setPos(int x, int y) {
        this.textField.setPos(x + width - 154, y);
        super.setPos(x, y);
    }

    public TextField getTextField() {
        return textField;
    }

    public void updateTextFieldText(String text) {
        textField.getField().setText(text);
    }

    private String formatText(Object configValue) {
        return config.inputType == Config.InputType.HEX_COLOR ? String.format("#%06X", (int) configValue) : configValue + "";
    }

    private static class InputField extends TextField {

        private final InputElement parent;

        public InputField(int x, int y, int width, int height, Text hint, InputElement parent, Consumer<String> changedListener) {
            super(x, y, width, height, hint, changedListener);
            this.parent = parent;
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return this.parent.isHovered() && super.isMouseOver(mouseX, mouseY);
        }
    }
}
