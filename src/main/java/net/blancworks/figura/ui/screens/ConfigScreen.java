package net.blancworks.figura.ui.screens;

import net.blancworks.figura.config.ConfigManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.blancworks.figura.ui.widgets.lists.ConfigList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ConfigScreen extends AbstractPanelScreen {

    public static final Text TITLE = new TranslatableText("figura.gui.panels.title.settings");

    private ConfigList list;

    public ConfigScreen(Screen parentScreen) {
        super(parentScreen, TITLE, 4);
    }

    @Override
    protected void init() {
        super.init();

        // -- bottom buttons -- //

        //apply
        this.addDrawableChild(new TexturedButton(width / 2 - 62, height - 24, 60, 20, new TranslatableText("figura.gui.settings.apply"), null, button -> {
            ConfigManager.applyConfig();
            ConfigManager.saveConfig();
            list.updateList();
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);
                super.renderButton(matrixStack, mouseX, mouseY, delta);
            }
        });

        //discard
        this.addDrawableChild(new TexturedButton(width / 2 + 2, height - 24, 60, 20, new TranslatableText("figura.gui.settings.discard"), null, button -> {
            ConfigManager.discardConfig();
            list.updateList();
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
                UIHelper.renderSliced(matrixStack, x, y, width, height, UIHelper.OUTLINE);
                super.renderButton(matrixStack, mouseX, mouseY, delta);
            }
        });

        // -- config list -- //

        list = new ConfigList(4, 32, width - 8, height - 60);
        this.addDrawableChild(list);
    }

    @Override
    public void removed() {
        ConfigManager.discardConfig();
        super.removed();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        KeyBinding bind = list.focusedBinding;
        //attempt to set keybind
        if (bind != null) {
            bind.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
            list.focusedBinding = null;
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        KeyBinding bind = list.focusedBinding;
        //attempt to set keybind
        if (bind != null) {
            bind.setBoundKey(keyCode == 256 ? InputUtil.UNKNOWN_KEY: InputUtil.fromKeyCode(keyCode, scanCode));
            list.focusedBinding = null;
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
