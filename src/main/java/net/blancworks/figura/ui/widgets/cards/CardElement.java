package net.blancworks.figura.ui.widgets.cards;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.StencilHelper;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

public class CardElement {

    //textures
    public static final Identifier VIEWPORT = new Identifier("figura", "textures/cards/viewport.png");
    public static final Identifier OVERLAY = new Identifier("figura", "textures/cards/overlay.png");
    public static final Identifier BACK_ART = new Identifier("figura", "textures/cards/back.png");

    private final CardBackgroundElement background;

    //fields
    protected final Vec3f color;
    protected Vec2f rot = new Vec2f(0f, 0f);

    //stencil
    public final StencilHelper stencil = new StencilHelper();

    public CardElement(CardBackgroundElement.BackgroundType background, Vec3f color, int stencilID) {
        this.background = new CardBackgroundElement(background, this);
        this.color = color;
        this.stencil.stencilLayerID = stencilID;
    }

    //tick
    public void tick() {
        background.tick();
    }

    //render
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        matrixStack.push();

        //rotate card
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rot.y));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rot.x));

        try {
            //get top-left for draw
            matrixStack.push();
            matrixStack.translate(-32, -48, 0);

            // -- stencil viewport -- //

            //Prepare stencil by drawing an object where we want the card "viewport" to be
            stencil.setupStencilWrite();

            RenderSystem.setShaderTexture(0, VIEWPORT);
            UIHelper.drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

            //From here on out, we aren't allowed to draw pixels outside the viewport we created above ^
            stencil.setupStencilTest();

            // -- background and content -- //

            //background
            background.render(matrixStack);

            //render card contents
            renderCardContent(matrixStack, mouseX, mouseY, delta);

            //After this point, the stencil buffer is *effectively* turned off.
            //No values will be written to the stencil buffer, and all objects will render
            //regardless of what's in the buffer.
            stencil.resetStencilState();

            //render card overlays
            renderOverlay(matrixStack, mouseX, mouseY, delta);

            // -- back art, overlay and texts -- //

            //render back art
            if (rot.x > 90 || rot.x < -90 || rot.y > 90 || rot.y < -90) {
                RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), 1f);
                RenderSystem.setShaderTexture(0, BACK_ART);

                matrixStack.push();
                matrixStack.translate(64f, 0f, 0f);
                matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
                UIHelper.drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
                matrixStack.pop();
            }

            matrixStack.pop();
        } catch (Exception e) {
            e.printStackTrace();
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        matrixStack.pop();
    }

    protected void renderCardContent(MatrixStack stack, int mouseX, int mouseY, float delta) {}

    protected void renderOverlay(MatrixStack stack, int mouseX, int mouseY, float delta) {}

    public void setRotation(float x, float y) {
        this.rot = new Vec2f(x, y);
    }

    public CardBackgroundElement getBackground() {
        return background;
    }
}