package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.screens.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PanelSelectorWidget extends AbstractParentElement implements Drawable, Selectable {

    private final List<ClickableWidget> children = new ArrayList<>();

    public PanelSelectorWidget(Screen parentScreen, int x, int y, int width, int selected) {
        //buttons
        ArrayList<SwitchButton> buttons = new ArrayList<>();

        int pos = x + width / 6;
        createPanelButton(buttons, () -> new ProfileScreen(parentScreen), ProfileScreen.TITLE, pos - 30, y);
        createPanelButton(buttons, () -> new BrowserScreen(parentScreen), BrowserScreen.TITLE, pos * 2 - 30, y);
        createPanelButton(buttons, () -> new WardrobeScreen(parentScreen), WardrobeScreen.TITLE, pos * 3 - 30, y);
        createPanelButton(buttons, () -> new TrustScreen(parentScreen), TrustScreen.TITLE, pos * 4 - 30, y);
        createPanelButton(buttons, () -> new SettingsScreen(parentScreen), SettingsScreen.TITLE, pos * 5 - 30, y);

        //selected button
        buttons.get(selected).setToggled(true);

        //TODO - remove this when we actually implement those panels
        for (int i = 0; i < 2; i++) {
            SwitchButton button = buttons.get(i);
            button.setTooltip(new LiteralText("Not yet ❤"));
            button.active = false;
        }
    }

    private void createPanelButton(ArrayList<SwitchButton> list, Supplier<AbstractPanelScreen> screenSupplier, Text title, int x, int y) {
        //create button
        SwitchButton button = new SwitchButton(x, y + 4, 60, 20, title, null, bx -> MinecraftClient.getInstance().setScreen(screenSupplier.get()));

        //add button
        list.add(button);
        children.add(button);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        for (ClickableWidget widget : children) {
            widget.render(matrices, mouseX, mouseY, delta);
        }
    }

    @Override
    public List<? extends Element> children() {
        return children;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
