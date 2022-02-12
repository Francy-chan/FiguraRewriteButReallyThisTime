package net.blancworks.figura.ui.cards;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.StencilHelper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;

import java.util.ArrayList;
import java.util.List;

public class CardElement extends DrawableHelper {

    //stencil
    private final StencilHelper stencil = new StencilHelper();

    //textures
    public static final Identifier VIEWPORT = new Identifier("figura", "textures/cards/viewport.png");
    public static final Identifier BACK_ART = new Identifier("figura", "textures/cards/back.png");
    public static final Identifier OVERLAY = new Identifier("figura", "textures/cards/overlay.png");
    public static final List<Identifier> BACKGROUND = new ArrayList<>() {{
        for (int i = 0; i < 7; i++) {
            add(new Identifier("figura", "textures/cards/background/layer" + i + ".png"));
        }
    }};

    //fields
    private final Vec3f color;

    private Vec2f rot = new Vec2f(0f, 0f);

    public CardElement(Vec3f color, int stencilID) {
        this.color = color;
        this.stencil.stencilLayerID = stencilID;
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
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

            //From here on out, we aren't allowed to draw pixels outside the viewport we created above ^
            stencil.setupStencilTest();

            // -- background and content -- //

            //background
            renderBackground(matrixStack);

            //card entity
            renderCardContent(matrixStack, mouseX, mouseY, delta);

            //After this point, the stencil buffer is *effectively* turned off.
            //No values will be written to the stencil buffer, and all objects will render
            //regardless of what's in the buffer.
            stencil.resetStencilState();

            // -- back art, overlay and texts -- //

            //render back art
            RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), 1f);
            RenderSystem.setShaderTexture(0, BACK_ART);

            matrixStack.push();
            matrixStack.translate(64f, 0f, 0f);
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            drawTexture(matrixStack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);
            matrixStack.pop();

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

    protected void renderBackground(MatrixStack matrixStack) {
        RenderSystem.setShaderColor(color.getX(), color.getY(), color.getZ(), 1f);

        float parallax = 1.5f;
        for (int i = 0; i < BACKGROUND.size(); i++, parallax -= 0.15f) {
            RenderSystem.setShaderTexture(0, BACKGROUND.get(i));

            matrixStack.push();
            float x = MathHelper.clamp(((-rot.x * parallax) / 90) * 48, -48, 48);
            float y = MathHelper.clamp(((rot.y * parallax) / 90) * 32, -32, 32);
            matrixStack.translate(x, y, 0);

            drawTexture(matrixStack, -48, -32, 160, 160, 0, 0, 160, 160, 160, 160);
            matrixStack.pop();
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

    public void setRotation(float x, float y) {
        this.rot = new Vec2f(x, y);
    }
}