package net.blancworks.figura.ui.cards;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.StencilHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;

public class CardElement extends DrawableHelper {

    //stencil
    private final StencilHelper stencil = new StencilHelper();

    //textures
    private static final Identifier VIEWPORT = new Identifier("figura", "textures/cards/viewport.png");
    private static final Identifier BACK_ART = new Identifier("figura", "textures/cards/back.png");
    private static final Identifier OVERLAY = new Identifier("figura", "textures/cards/overlay.png");
    private static final List<Identifier> BACKGROUND = new ArrayList<>() {{
        for (int i = 0; i < 7; i++) {
            add(new Identifier("figura", "textures/cards/background/layer" + i + ".png"));
        }
    }};

    //fields
    private final int color;
    private final Text name;
    private final Text author;

    private final int stencilLayerID;

    public CardElement(Text name, Text author, int color, int stencilLayerID) {
        this.name = name;
        this.author = author;
        this.color = color;
        this.stencilLayerID = stencilLayerID;
    }

    //render
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        matrixStack.push();
        RenderSystem.setShaderColor((color & 255) / 255f, ((color >> 8) & 255) / 255f, ((color >> 16) & 255) / 255f, 1f);

        try {
            //center rotation
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            stencil.setupStencilWrite();

            RenderSystem.setShaderTexture(0, VIEWPORT);
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

            //From here on out, we aren't allowed to draw pixels outside the viewport we created above ^
            stencil.setupStencilTest();

            //background
            renderBackground(matrixStack, mouseX, mouseY, delta);

            //card entity
            renderCardContent(matrixStack, mouseX, mouseY, delta);

            //After this point, the stencil buffer is *effectively* turned off.
            //No values will be written to the stencil buffer, and all objects will render
            //regardless of what's in the buffer.
            stencil.resetStencilState();

            //render back art
            RenderSystem.setShaderTexture(0, BACK_ART);

            matrixStack.push();
            matrixStack.translate(64f, 0f, 0f);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
            matrixStack.pop();

            //render overlay
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderTexture(0, OVERLAY);

            matrixStack.push();
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
            matrixStack.pop();

            //render texts
            MinecraftClient client = MinecraftClient.getInstance();

            //name
            matrixStack.push();
            matrixStack.translate(3f, 3f, 2f); //3px offset
            String nameString = client.textRenderer.trimToWidth(name.getString(), 59); // 64 - 3 - 2
            drawStringWithShadow(matrixStack, client.textRenderer, nameString, 0, 0, 0xFFFFFF);
            matrixStack.pop();

            //author
            matrixStack.push();
            matrixStack.translate(3f, 11f, 2f); //3px offset + 7px above text + 1px spacing
            matrixStack.scale(0.75f, 0.75f,1f);
            String authorString = client.textRenderer.trimToWidth(author.getString(), 75); //64 + 64 * 0.75 - 3 - 2
            drawStringWithShadow(matrixStack, client.textRenderer, authorString, 0, 0, 0xFFFFFF);
            matrixStack.pop();

            matrixStack.pop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        matrixStack.pop();
    }

    protected void renderCardContent(MatrixStack stack, int mouseX, int mouseY, float delta) {

    }

    private void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        matrixStack.push();
        for (int i = 0; i < 7; i++) {
            RenderSystem.setShaderTexture(0, BACKGROUND.get(i));
            drawTexture(matrixStack, -48, -32, 160, 160, 0, 0, 160, 160, 160, 160);
        }
        matrixStack.pop();
    }
}