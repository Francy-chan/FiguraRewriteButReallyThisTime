package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class SwitchButton extends TexturedButton {

    public static final Identifier SWITCH_TEXTURE = new Identifier("figura", "textures/gui/switch.png");

    protected boolean toggled = false;
    private boolean defaultTexture = false;
    private float headPos = 0f;

    //text constructor
    public SwitchButton(int x, int y, int width, int height, Text text, Text tooltip, PressAction pressAction) {
        super(x, y, width, height, text, tooltip, pressAction);
    }

    //texture constructor
    public SwitchButton(int x, int y, int width, int height, int u, int v, int interactionOffset, Identifier texture, int textureWidth, int textureHeight, Text tooltip, PressAction pressAction) {
        super(x, y, width, height, u, v, interactionOffset, texture, textureWidth, textureHeight, tooltip, pressAction);
    }

    //default texture constructor
    public SwitchButton(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, 0, 0, 10, SWITCH_TEXTURE, 20, 40, null, button -> {});
        this.toggled = toggled;
        this.headPos = toggled ? 20f : 0f;
        defaultTexture = true;
    }

    @Override
    public void onPress() {
        this.toggled = !this.toggled;
        super.onPress();
    }

    @Override
    protected void renderTexture(MatrixStack matrixStack, float delta) {
        if (defaultTexture) {
            renderDefaultTexture(matrixStack, delta);
        } else {
            super.renderTexture(matrixStack, delta);
        }
    }

    @Override
    protected void renderText(MatrixStack matrixStack) {
        //get text color
        int color;
        if (!this.active) color = Formatting.DARK_GRAY.getColorValue();
        else if (this.hovered || this.toggled) color = Formatting.WHITE.getColorValue();
        else color = Formatting.GRAY.getColorValue();

        //draw text
        drawCenteredTextWithShadow(
                matrixStack, MinecraftClient.getInstance().textRenderer,
                (this.toggled ? text.copy().formatted(Formatting.UNDERLINE) : text).asOrderedText(),
                this.x + this.width / 2, this.y + this.height / 2 - 4,
                color
        );
    }

    protected void renderDefaultTexture(MatrixStack matrixStack, float delta) {
        //set texture
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SWITCH_TEXTURE);

        //render switch
        drawTexture(matrixStack, x + 5, y + 5, 20, 10, 0f, this.hovered ? 10f : 0f, 20, 10, 20, 40);

        //render head
        headPos = (float) MathHelper.lerp(1f - Math.pow(0.2f, delta), headPos, this.toggled ? 20f : 0f);
        drawTexture(matrixStack, Math.round(x + headPos), y, 10, 20, this.hovered ? 10f : 0f, 20f, 10, 20, 20, 40);
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}
