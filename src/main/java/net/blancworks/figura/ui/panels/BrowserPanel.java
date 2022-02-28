package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class BrowserPanel extends Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/browser.png");

    public BrowserPanel() {
        super(new TranslatableText("figura.gui.panels.title.browser").formatted(Formatting.RED));
    }

    @Override
    public void init() {
        FiguraToast.sendToast("not yet!", "<3");

        int y = -72;
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("default toast"), button -> {
            FiguraToast.sendToast("default", "test", FiguraToast.ToastType.DEFAULT);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("error toast"), button -> {
            FiguraToast.sendToast("error", "test", FiguraToast.ToastType.ERROR);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y += 24), 60, 20, new LiteralText("warning toast"), button -> {
            FiguraToast.sendToast("warning", "test", FiguraToast.ToastType.WARNING);
        }));
        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + (y + 24), 60, 20, new LiteralText("cheese toast"), button -> {
            FiguraToast.sendToast("cheese", "test", FiguraToast.ToastType.CHEESE);
        }));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);
        super.render(stack, mouseX, mouseY, delta);
    }
}
