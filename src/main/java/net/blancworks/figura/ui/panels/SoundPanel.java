package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.FiguraToast;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;

public class SoundPanel extends Panel {

    private final Panel parentPanel;

    public SoundPanel(Panel parentPanel) {
        super(new TranslatableText("figura.gui.panels.title.sound"));
        this.parentPanel = parentPanel;
    }

    @Override
    protected void init() {
        FiguraToast.sendToast(new LiteralText("lol nope").setStyle(Style.EMPTY.withColor(0xFFADAD)), FiguraToast.ToastType.DEFAULT);

        this.addDrawableChild(new TexturedButton(width / 2 - 30, height / 2 - 30, 60, 20, new LiteralText("test2"), new LiteralText("test2"), button -> {
            FiguraToast.sendToast("test2", "test2", FiguraToast.ToastType.DEFAULT);
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
