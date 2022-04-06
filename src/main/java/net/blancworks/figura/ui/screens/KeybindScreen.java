package net.blancworks.figura.ui.screens;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class KeybindScreen extends AbstractPanelScreen {

    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.keybind");

    public KeybindScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 2);
    }

    @Override
    protected void init() {
        super.init();

        FiguraToast.sendToast(new LiteralText("lol nope").setStyle(Style.EMPTY.withColor(0xFFADAD)), FiguraToast.ToastType.DEFAULT);

        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + 10, 60, 20, new LiteralText("test"), new LiteralText("test"), button -> {
            FiguraToast.sendToast("test", "test", FiguraToast.ToastType.DEFAULT);
        }));
    }
}
