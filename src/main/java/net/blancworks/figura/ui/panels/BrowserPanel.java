package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class BrowserPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/browser.png");

    public BrowserPanel() {
        super(new TranslatableText("figura.gui.panels.title.browser").formatted(Formatting.RED));
    }

    @Override
    public void init() {
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

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
