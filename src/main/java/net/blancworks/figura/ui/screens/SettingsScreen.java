package net.blancworks.figura.ui.screens;

import net.blancworks.figura.config.ConfigManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class SettingsScreen extends AbstractPanelScreen {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background/settings.png");
    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.settings");

    public SettingsScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 4);
    }

    @Override
    protected void init() {
        super.init();

        // -- config list -- //

        this.addDrawableChild(new ConfigList(4, 32, width - 8, height - 60));

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
    }

    @Override
    public Identifier getBackground() {
        return BACKGROUND;
    }
}
