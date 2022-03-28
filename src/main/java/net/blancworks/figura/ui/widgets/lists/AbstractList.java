package net.blancworks.figura.ui.widgets.lists;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.ScrollBarWidget;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractList extends AbstractParentElement implements Drawable, Selectable {

    protected final List<Element> children = new ArrayList<>();
    protected final ScrollBarWidget scrollBar;

    public int x, y;
    public int width, height;

    public int scissorsX, scissorsY;
    public int scissorsWidth, scissorsHeight;

    protected AbstractList(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        updateScissors(1, 1, -2, -2);

        scrollBar = new ScrollBarWidget(x + width - 14, y + 4, 10, height - 8, 0f);
        children.add(scrollBar);
    }

    public void updateScissors(int xOffset, int yOffset, int endXOffset, int endYOffset) {
        this.scissorsX = xOffset;
        this.scissorsY = yOffset;
        this.scissorsWidth = endXOffset;
        this.scissorsHeight = endYOffset;
    }

    public boolean isInsideScissors(double mouseX, double mouseY) {
        return UIHelper.isMouseOver(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (Element element : children) {
            if (element instanceof Drawable drawable && !contents().contains(element))
                drawable.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return scrollBar.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
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
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    public List<? extends Element> contents() {
        return Collections.emptyList();
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
