package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

public class KeybindPanel extends Panel {

    private final Panel parentPanel;

    public KeybindPanel(Panel parentPanel) {
        super(new TranslatableText("figura.gui.panels.title.keybind"));
        this.parentPanel = parentPanel;
    }

    @Override
    protected void init() {
        FiguraToast.sendToast(new LiteralText("lol nope").setStyle(Style.EMPTY.withColor(0xFFADAD)), FiguraToast.ToastType.DEFAULT);

        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 + 10, 60, 20, new LiteralText("test"), new LiteralText("test"), button -> {
            FiguraToast.sendToast("test", "test", FiguraToast.ToastType.DEFAULT);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            parentPanel.setVisible(true);
            parentPanel.setChildScreen(null);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
