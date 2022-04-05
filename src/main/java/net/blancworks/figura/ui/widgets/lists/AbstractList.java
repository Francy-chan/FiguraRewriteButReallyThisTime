package net.blancworks.figura.ui.widgets.lists;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.AbstractParentElement;
import net.blancworks.figura.ui.widgets.ScrollBarWidget;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;
import java.util.List;

public abstract class AbstractList extends AbstractParentElement {

    protected final ScrollBarWidget scrollBar;

    public int scissorsX, scissorsY;
    public int scissorsWidth, scissorsHeight;

    public AbstractList(int x, int y, int width, int height) {
        super(x, y, width, height);

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
        if (scrollBar.mouseScrolled(mouseX, mouseY, amount)) {
            //hide context
            if (contextMenu != null)
                contextMenu.setVisible(false);

            return true;
        } else {
            return super.mouseScrolled(mouseX, mouseY, amount);
        }
    }

    public List<? extends Element> contents() {
        return Collections.emptyList();
    }
}
