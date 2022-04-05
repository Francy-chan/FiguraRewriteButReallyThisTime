package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class TextField extends AbstractParentElement {

    private final Text hint;
    private final TextFieldWidget field;
    private int borderColour = 0xFFFFFFFF;

    public TextField(int x, int y, int width, int height, Text hint, Consumer<String> changedListener) {
        super(x, y, width, height);
        this.hint = hint;

        field = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 4, y + (height - 8) / 2, width - 12, height - (height - 8) / 2, LiteralText.EMPTY);
        field.setMaxLength(32767);
        field.setDrawsBackground(false);
        field.setChangedListener(changedListener);
        children.add(field);
    }

    @Override
    public void tick() {
        field.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!isVisible()) return;

        //render background
        UIHelper.fillRounded(matrices, x, y, width, height, this.isMouseOver(mouseX, mouseY) ? 0x60FFFFFF : 0xFF000000);
        UIHelper.fillOutline(matrices, x, y, width, height, field.isFocused() ? borderColour : 0xFF404040);

        //hint text
        if (hint != null && field.getText().isEmpty() && !field.isFocused()) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(
                    matrices, hint.copy().formatted(Formatting.DARK_GRAY, Formatting.ITALIC),
                    this.x + 4, this.y + (height - 8f) / 2f, 0xFFFFFF
            );
        }
        //input text
        else {
            field.renderButton(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //mouse over check
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        //hacky
        mouseX = MathHelper.clamp(mouseX, field.x, field.x + field.getWidth() - 1);
        mouseY = MathHelper.clamp(mouseY, field.y, field.y + field.getHeight() - 1);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        this.field.x = x + 4;
        this.field.y = y + (this.height - 8) / 2;
    }

    public void setBorderColour(int borderColour) {
        this.borderColour = borderColour;
    }

    public TextFieldWidget getField() {
        return field;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
        field.appendNarrations(builder);
    }

    @Override
    public SelectionType getType() {
        return field.getType();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.field.setTextFieldFocused(false);
    }
}
