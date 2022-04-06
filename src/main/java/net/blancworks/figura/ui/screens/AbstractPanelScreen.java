package net.blancworks.figura.ui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.FiguraTickable;
import net.blancworks.figura.ui.widgets.PanelSelectorWidget;
import net.blancworks.figura.ui.widgets.TexturedButton;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3f;

public abstract class AbstractPanelScreen extends Screen {

    public static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/background.png");

    //variables
    protected final Screen parentScreen;
    private final int index;

    //widgets control
    protected TexturedButton helpButton;
    protected TexturedButton backButton;

    protected AbstractPanelScreen(Screen parentScreen, Text title, int index) {
        super(title);
        this.parentScreen = parentScreen;
        this.index = index;
    }

    @Override
    protected void init() {
        super.init();

        //add help button
        helpButton = new TexturedButton(
                4, 4, 30, 20,
                20, 0, 20,
                new Identifier("figura", "textures/gui/help.png"),
                40, 40,
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
        );
        this.addDrawableChild(helpButton);

        //add panel selector
        this.addDrawableChild(new PanelSelectorWidget(parentScreen, 0, 0, width, index));

        //add back button
        backButton = new TexturedButton(
                width - 34, 4, 30, 20,
                20, 0, 20,
                new Identifier("figura", "textures/gui/back.png"),
                40, 40,
                new TranslatableText("figura.gui.back.tooltip"),
                bx -> MinecraftClient.getInstance().setScreen(parentScreen)
        );
        this.addDrawableChild(backButton);
    }

    @Override
    public void tick() {
        for (Element element : this.children()) {
            if (element instanceof FiguraTickable tickable)
                tickable.tick();
        }

        super.tick();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        //setup figura framebuffer
        UIHelper.useFiguraGuiFramebuffer();

        //render background
        this.renderBackground();

        //render contents
        super.render(matrixStack, mouseX, mouseY, delta);

        //restore vanilla framebuffer
        UIHelper.useVanillaFramebuffer(matrixStack);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parentScreen);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //yeet mouse 0 and isDragging check
        return this.getFocused() != null && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        //better check for mouse released when outside element boundaries
        return this.getFocused() != null && this.getFocused().mouseReleased(mouseX, mouseY, button);
    }

    public void renderBackground() {
        //rainbow
        Vec3f color = ColorUtils.hsvToRGB(new Vec3f((FiguraMod.ticks * 2) % 256 / 255f, 0.7f, 1f));
        RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), 1f);

        //render
        float textureSize = (float) (64f / MinecraftClient.getInstance().getWindow().getScaleFactor());
        UIHelper.renderBackgroundTexture(BACKGROUND, 0, 0, this.width, this.height, textureSize, textureSize);

        //reset rainbow
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
