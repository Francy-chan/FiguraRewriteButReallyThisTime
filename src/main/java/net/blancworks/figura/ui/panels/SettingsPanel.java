package net.blancworks.figura.ui.panels;

import net.blancworks.figura.config.ConfigManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.ConfigList;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SettingsPanel extends Panel {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/settings.png");

    public SettingsPanel() {
        super(new TranslatableText("figura.gui.panels.title.settings"));
    }

    @Override
    protected void init() {
        super.init();

        // -- bottom buttons -- //

        //apply
        this.addDrawableChild(new TexturedButton(width / 2 - 62, height - 24, 60, 20, new TranslatableText("figura.gui.settings.apply"), null, button -> {
            ConfigManager.applyConfig();
            ConfigManager.saveConfig();
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);
                super.renderButton(matrixStack, mouseX, mouseY, delta);
            }
        });

        //discard
        this.addDrawableChild(new TexturedButton(width / 2 + 2, height - 24, 60, 20, new TranslatableText("figura.gui.settings.discard"), null, button -> ConfigManager.discardConfig()) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);
                super.renderButton(matrixStack, mouseX, mouseY, delta);
            }
        });

        // -- config list -- //

        this.addDrawableChild(new ConfigList(4, 32, width - 8, height - 60));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //render background
        renderBackground();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        ConfigManager.discardConfig();
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
