package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParentElement extends net.minecraft.client.gui.AbstractParentElement implements Drawable, FiguraDrawable, Selectable, FiguraTickable {

    protected final List<Element> children = new ArrayList<>();

    public int x, y;
    public int width, height;

    private boolean visible = true;

    public AbstractParentElement(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void tick() {
        for (Element element : this.children) {
            if (element instanceof FiguraTickable tickable)
                tickable.tick();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (Element element : children) {
            if (element instanceof Drawable drawable)
                drawable.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //fix mojang focusing for text fields
        for (Element element : this.children()) {
            if (element instanceof TextField field)
                field.getField().setTextFieldFocused(field.isMouseOver(mouseX, mouseY));
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //yeet mouse 0 and isDragging check
        return this.getFocused() != null && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        //better check for mouse released when outside node's boundaries
        return this.getFocused() != null && this.getFocused().mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;

        for (Element element : children()) {
            if (element instanceof FiguraDrawable drawable)
                drawable.setVisible(visible);
            else if (element instanceof ClickableWidget widget)
                widget.visible = visible;
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
