package net.blancworks.figura.ui.screens;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class BrowserScreen extends AbstractPanelScreen {

    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.browser").formatted(Formatting.RED);

    public BrowserScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 1);
    }

    @Override
    public void init() {
        super.init();

        FiguraToast.sendToast("not yet!", "<3");

        int y = -72;
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("default toast"), new TranslatableText("figura.backend.error"), button -> {
            FiguraToast.sendToast("default", "test", FiguraToast.ToastType.DEFAULT);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("error toast"), new LiteralText("test2"), button -> {
            FiguraToast.sendToast("error", "test", FiguraToast.ToastType.ERROR);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("warning toast"), new LiteralText("test3\novo"), button -> {
            FiguraToast.sendToast("warning", "test", FiguraToast.ToastType.WARNING);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y + 24), 60, 20, new LiteralText("cheese toast"), new LiteralText("test4\n\nhehe"), button -> {
            FiguraToast.sendToast("cheese", "test", FiguraToast.ToastType.CHEESE);
        }));
    }
}
