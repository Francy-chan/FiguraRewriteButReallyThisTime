package net.blancworks.figura.ui.widgets.trust;

import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.widgets.lists.TrustContainerList;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class AbstractContainerElement extends PressableWidget implements Comparable<AbstractContainerElement> {

    protected final TrustContainerList parent;
    protected final TrustContainer trust;

    protected float scale = 1f;

    protected AbstractContainerElement(int height, TrustContainer container, TrustContainerList parent) {
        super(0, 0, 174, height, LiteralText.EMPTY);
        this.parent = parent;
        this.trust = container;
    }

    protected void animate(int mouseX, int mouseY, float delta) {
        if (this.isMouseOver(mouseX, mouseY) || this.isFocused()) {
            scale = (float) MathHelper.lerp(1 - Math.pow(0.2, delta), scale, 1.2f);
        } else {
            scale = (float) MathHelper.lerp(1 - Math.pow(0.3, delta), scale, 1f);
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.parent.isInsideScissors(mouseX, mouseY) && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void onPress() {
        //set selected entry
        parent.selectedEntry = this;

        //update trust widgets
        parent.parent.updateTrustData(this.trust);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    public boolean isVisible() {
        return trust.getParentGroup().visible;
    }

    public TrustContainer getTrust() {
        return trust;
    }

    @Override
    public int compareTo(AbstractContainerElement other) {
        //compare trust levels first
        ArrayList<TrustContainer> list = new ArrayList<>(TrustManager.GROUPS.values());
        int comp = Integer.compare(list.indexOf(this.trust.getParentGroup()), list.indexOf(other.trust.getParentGroup()));

        //then compare types
        if (comp == 0) {
            if (this instanceof GroupElement && other instanceof PlayerElement)
                return -1;
            else if (this instanceof PlayerElement && other instanceof GroupElement)
                return 1;
        }

        //and then compare names
        if (comp == 0 && this instanceof PlayerElement player1 && other instanceof PlayerElement player2)
             return player1.getName().compareTo(player2.getName());

        //return
        return comp;
    }
}
