package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.screens.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.Supplier;

public class PanelSelectorWidget extends AbstractParentElement {

    public PanelSelectorWidget(Screen parentScreen, int x, int y, int width, int selected) {
        super(x, y, width, 20);

        //buttons
        ArrayList<SwitchButton> buttons = new ArrayList<>();

        int pos = x + width / 6;
        createPanelButton(buttons, () -> new ProfileScreen(parentScreen), ProfileScreen.TITLE, pos - 30, y);
        createPanelButton(buttons, () -> new BrowserScreen(parentScreen), BrowserScreen.TITLE, pos * 2 - 30, y);
        createPanelButton(buttons, () -> new WardrobeScreen(parentScreen), WardrobeScreen.TITLE, pos * 3 - 30, y);
        createPanelButton(buttons, () -> new TrustScreen(parentScreen), TrustScreen.TITLE, pos * 4 - 30, y);
        createPanelButton(buttons, () -> new ConfigScreen(parentScreen), ConfigScreen.TITLE, pos * 5 - 30, y);

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
        button.shouldHaveBackground(false);

        //add button
        list.add(button);
        children.add(button);
    }
}
