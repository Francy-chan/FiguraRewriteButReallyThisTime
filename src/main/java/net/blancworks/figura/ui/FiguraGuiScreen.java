package net.blancworks.figura.ui;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.*;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.ArrayList;
import java.util.function.Supplier;

public class FiguraGuiScreen extends Panel {

    private final Screen parentScreen;
    private int lastPanel = 2; //init as wardrobe

    public FiguraGuiScreen(Screen parentScreen) {
        super(LiteralText.EMPTY);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        //buttons
        ArrayList<TexturedButton> buttons = new ArrayList<>();

        int x = this.width / 6;
        createPanelButton(buttons, ProfilePanel::new, x - 30);
        createPanelButton(buttons, BrowserPanel::new, x * 2 - 30);
        createPanelButton(buttons, WardrobePanel::new, x * 3 - 30);
        createPanelButton(buttons, TrustPanel::new, x * 4 - 30);
        createPanelButton(buttons, SettingsPanel::new, x * 5 - 30);

        //init last panel
        buttons.get(lastPanel).onClick(0, 0);

        //help button
        this.addDrawableChild(new TexturedButton(
                4, 4, 24, 24,
                24, 0, 24,
                new Identifier("figura", "textures/gui/help.png"),
                48, 48,
                new TranslatableText("figura.gui.help.tooltip"),
                bx -> {
                    String url = "https://github.com/Blancworks/FiguraRewrite/";
                    MinecraftClient.getInstance().setScreen(new ConfirmChatLinkScreen((bl) -> {
                        if (bl) {
                            Util.getOperatingSystem().open(url);
                        }
                        MinecraftClient.getInstance().setScreen(this);
                    }, url, true));
                }
        ));

        //back button
        this.addDrawableChild(new TexturedButton(
                width - 28, 4, 24, 24,
                24, 0, 24,
                new Identifier("figura", "textures/gui/back.png"),
                48, 48,
                new TranslatableText("figura.gui.back.tooltip"),
                bx -> MinecraftClient.getInstance().setScreen(parentScreen)
        ));
    }

    private void createPanelButton(ArrayList<TexturedButton> list, Supplier<Panel> panelProvider, int x) {
        Panel tmp = panelProvider.get();
        int index = list.size();

        //create button
        TexturedButton button = new TexturedButton(x, 4, 60, 20, tmp.getTitle(), null, bx -> {
            //TODO - remove when we actually implement them
            Panel panel = panelProvider.get();
            if (panel instanceof ProfilePanel || panel instanceof BrowserPanel) {
                FiguraToast.sendToast("not yet!", "<3");
                return;
            }
            //remove up to here

            setChildScreen(panelProvider.get());

            //button logic
            for (TexturedButton butt : list)
                butt.setToggled(false);

            ((TexturedButton) bx).setToggled(true);
            lastPanel = index;
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
        MinecraftClient.getInstance().setScreen(parentScreen);
        this.lastPanel = 2;
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
