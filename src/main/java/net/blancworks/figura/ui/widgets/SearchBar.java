package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SearchBar extends AbstractParentElement implements Drawable, FiguraDrawable, Selectable {

    private final Text hint;
    private final List<TextFieldWidget> children = new ArrayList<>();
    private final TextFieldWidget field;

    private boolean visible = true;

    public int x, y;
    public int width, height;

    public SearchBar(int x, int y, int width, int height, Text hint, Consumer<String> changedListener) {
        field = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, x + 4, y + (height - 8) / 2, width - 12, height - (height - 8) / 2, LiteralText.EMPTY);
        field.setMaxLength(32767);
        field.setDrawsBackground(false);
        field.setChangedListener(changedListener);
        children.add(field);

        this.hint = hint;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void tick() {
        this.field.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //render background
        UIHelper.fillRounded(matrices, x, y, width, height, 0xFF000000);
        UIHelper.fillOutline(matrices, x, y, width, height, field.isFocused() ? 0xFFFFFFFF : 0xFF404040);

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
        //hacky
        if (UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY)) {
            mouseX = MathHelper.clamp(mouseX, field.x, field.x + field.getWidth() - 1);
            mouseY = MathHelper.clamp(mouseY, field.y, field.y + field.getHeight() - 1);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
        this.field.x = x + 4;
        this.field.y = y + (this.height - 8) / 2;
    }

    @Override
    public List<? extends Element> children() {
        return children;
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
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        this.field.setTextFieldFocused(false);
    }
}
