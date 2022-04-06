package net.blancworks.figura.ui.screens;

import net.blancworks.figura.ui.FiguraToast;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ProfileScreen extends AbstractPanelScreen {

    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.profile").formatted(Formatting.RED);

    public ProfileScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 0);
    }

    @Override
    public void init() {
        super.init();

        FiguraToast.sendToast("not yet!", "<3");
    }
}
