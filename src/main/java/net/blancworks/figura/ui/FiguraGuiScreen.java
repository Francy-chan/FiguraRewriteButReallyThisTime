package net.blancworks.figura.ui;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.*;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.function.Supplier;

public class FiguraGuiScreen extends Panel {

    private final Screen parentScreen;

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
        createPanelButton(buttons, HelpPanel::new, x);
        createPanelButton(buttons, BrowserPanel::new, x += 70);
        createPanelButton(buttons, WardrobePanel::new, x += 70);
        createPanelButton(buttons, TrustPanel::new, x += 70);
        createPanelButton(buttons, SettingsPanel::new, x + 70);

        //init as wardrobe
        buttons.get(2).onClick(0, 0);
    }

    private void createPanelButton(ArrayList<TexturedButton> list, Supplier<Panel> panelProvider, int x) {
        Panel tmp = panelProvider.get();

        //create button
        TexturedButton button = new TexturedButton(x, 4, 60, 20, tmp.getTitle(), null, bx -> {
            setChildScreen(panelProvider.get());

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
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void close() {
        super.close();
        this.client.setScreen(parentScreen);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //setup figura framebuffer
        UIHelper.useFiguraGuiFramebuffer();

        //render contents
        super.render(matrixStack, mouseX, mouseY, delta);

        //restore vanilla framebuffer
        UIHelper.useVanillaFramebuffer(matrixStack);
    }
}
