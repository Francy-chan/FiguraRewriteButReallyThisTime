package net.blancworks.figura.ui.widgets.config;

import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class KeybindElement extends AbstractConfigElement {

    private final KeyBinding binding;
    private final ParentedButton button;

    public KeybindElement(int width, Config config, ConfigList parent) {
        super(width, config, parent);
        this.binding = config.keyBind;

        //toggle button
        button = new ParentedButton(0, 0, 90, 20, this.binding.getBoundKeyLocalizedText(), this, button -> parent.focusedBinding = binding);
        children.add(0, button);

        //overwrite reset button to update the keybind
        children.remove(resetButton);
        resetButton = new ParentedButton(x + width - 60, y, 60, 20, new TranslatableText("controls.reset"), this, button -> binding.setBoundKey(binding.getDefaultKey()));
        children.add(resetButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!this.isVisible()) return;

        //reset enabled
        this.resetButton.active = !this.binding.isDefault();

        //button message
        button.setMessage(binding.getBoundKeyLocalizedText());

        //editing message
        if (parent.focusedBinding == this.binding) {
            button.setMessage(new LiteralText("> ").setStyle(ColorUtils.Colors.FRAN_PINK.style).append(button.getMessage()).append(" <"));
        }
        //conflict check
        else if (!this.binding.isUnbound()) {
            for (KeyBinding key : MinecraftClient.getInstance().options.allKeys) {
                if (key != this.binding && this.binding.equals(key)) {
                    button.setMessage(button.getMessage().shallowCopy().formatted(Formatting.RED));
                    break;
                }
            }
        }

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
