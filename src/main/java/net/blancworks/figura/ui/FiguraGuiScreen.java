package net.blancworks.figura.ui;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.*;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

public class FiguraGuiScreen extends Screen {

    private final Screen parentScreen;
    private Panel currentPanel;

    public FiguraGuiScreen(Screen parentScreen) {
        super(LiteralText.EMPTY);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        //buttons
        ArrayList<TexturedButton> buttons = new ArrayList<>();

        int x = this.width / 2 - 170;
        createPanelButton(buttons, new HelpPanel(), x);
        createPanelButton(buttons, new BrowserPanel(), x += 70);
        createPanelButton(buttons, new WardrobePanel(), x += 70);
        createPanelButton(buttons, new TrustPanel(), x += 70);
        createPanelButton(buttons, new SettingsPanel(), x + 70);

        //init as wardrobe
        buttons.get(2).onClick(0, 0);
    }

    private void createPanelButton(ArrayList<TexturedButton> list, Panel panel, int x) {
        //create button
        TexturedButton button = new TexturedButton(x, 0, 60, 20, panel.getName(), bx -> {
            //panel logic
            this.currentPanel = panel;
            panel.init();

            //button logic
            for (TexturedButton butt : list)
                butt.setSelected(false);

            ((TexturedButton) bx).setSelected(true);
        });

        //add button
        list.add(button);
        this.addDrawableChild(button);
    }

    @Override
    public void onClose() {
        this.client.setScreen(parentScreen);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //setup figura framebuffer
        UIHelper.useFiguraGuiFramebuffer(matrixStack);

        //render current panel
        currentPanel.render(matrixStack, mouseX, mouseY, delta);

        //buttons and stuff
        super.render(matrixStack, mouseX, mouseY, delta);

        //restore vanilla framebuffer
        UIHelper.useVanillaFramebuffer();
    }
}
