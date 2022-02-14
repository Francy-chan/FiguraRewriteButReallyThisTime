package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class BrowserPanel implements Panel {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/browser.png");

    @Override
    public void init() {
        FiguraToast.sendToast("not yet!", "<3");
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, BACKGROUND);
    }

    @Override
    public Text getName() {
        return new TranslatableText("figura.gui.panels.title.browser").formatted(Formatting.RED);
    }
}