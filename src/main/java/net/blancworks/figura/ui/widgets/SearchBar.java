package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class SearchBar extends TextFieldWidget {

    private final Text hint;

    public SearchBar(int x, int y, int width, int height, Text hint, Consumer<String> changedListener) {
        super(MinecraftClient.getInstance().textRenderer, x + 4, y + (height - 8) / 2, width - 12, height, LiteralText.EMPTY);
        this.setMaxLength(32767);
        this.setDrawsBackground(false);
        this.setChangedListener(changedListener);
        this.hint = hint;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        int y = this.y - (this.height - 8) / 2;
        UIHelper.fillRounded(matrices, x - 4, y, width + 12, height, 0xFF000000);
        UIHelper.fillOutline(matrices, x - 4, y, width + 12, height, this.isFocused() ? 0xFFFFFFFF : 0xFF404040);

        //hint text
        if (hint != null && getText().isEmpty() && !this.isFocused()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(
                    matrices, hint.copy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                    this.x, this.y, 0xFFFFFF
            );
        }
        //input text
        else {
            super.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers) || this.isFocused();
    }

    public void setPos(int x, int y) {
        this.x = x + 4;
        this.y = y + (height - 8) / 2;
    }
}
