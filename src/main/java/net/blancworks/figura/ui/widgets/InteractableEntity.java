package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class InteractableEntity extends ClickableWidget {

    public static final Identifier UNKNOWN = new Identifier("figura", "textures/gui/unknown.png");

    //fields
    private LivingEntity entity;
    private final float pitch, yaw, scale;

    //transformation data

    //rot
    private boolean isRotating = false;
    private float anchorX = 0f, anchorY = 0f;
    private float anchorAngleX = 0f, anchorAngleY = 0f;
    private float angleX, angleY;

    //scale
    private float scaledValue = 0f;
    private static final float SCALE_FACTOR = 1.1F;

    //pos
    private boolean isDragging = false;
    private int modelX, modelY;
    private float dragDeltaX, dragDeltaY;
    private float dragAnchorX, dragAnchorY;

    public InteractableEntity(int x, int y, int width, int height, int scale, float pitch, float yaw, LivingEntity entity) {
        super(x, y, width, height, LiteralText.EMPTY);
        this.scale = scale;
        this.pitch = pitch;
        this.yaw = yaw;
        this.entity = entity;

        modelX = width / 2;
        modelY = height / 2;
        angleX = pitch;
        angleY = yaw;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);

        //scissors
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        //render entity
        if (entity != null) {
            matrices.push();
            matrices.translate(0f, 0f, -400f);
            UIHelper.drawEntity(x + modelX, y + modelY, scale + scaledValue, angleX, angleY, entity, matrices);
            matrices.pop();
        } else {
            matrices.push();

            //transforms
            matrices.translate(x + modelX, y + modelY, 0f);
            float scale = this.scale / 35;
            matrices.scale(scale, scale, scale);
            matrices.multiply(Quaternion.fromEulerXyzDegrees(new Vec3f(angleX - pitch, angleY - yaw, 0f)));

            //draw front
            RenderSystem.setShaderTexture(0, UNKNOWN);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            drawTexture(matrices, -24, -32, 48, 64, 0f, 0f, 48, 64, 48, 64);

            //draw back
            matrices.push();
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            drawTexture(matrices, -24, -32, 48, 64, 0f, 0f, 48, 64, 48, 64);
            matrices.pop();

            matrices.pop();
        }

        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        switch (button) {
            //left click - rotate
            case 0 -> {
                //set anchor rotation

                //get starter mouse pos
                anchorX = (float) mouseX;
                anchorY = (float) mouseY;

                //get starter rotation angles
                anchorAngleX = angleX;
                anchorAngleY = angleY;

                isRotating = true;
                return true;
            }

            //right click - move
            case 1 -> {
                //get starter mouse pos
                dragDeltaX = (float) mouseX;
                dragDeltaY = (float) mouseY;

                //also get start node pos
                dragAnchorX = modelX;
                dragAnchorY = modelY;

                isDragging = true;
                return true;
            }

            //middle click - reset pos
            case 2 -> {
                isRotating = false;
                isDragging = false;
                anchorX = 0f;
                anchorY = 0f;
                anchorAngleX = 0f;
                anchorAngleY = 0f;
                angleX = pitch;
                angleY = yaw;
                scaledValue = 0f;
                modelX = width / 2;
                modelY = height / 2;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        //left click - stop rotating
        if (button == 0) {
            isRotating = false;
            return true;
        }

        //right click - stop dragging
        else if (button == 1) {
            isDragging = false;
            return true;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //left click - rotate
        if (isRotating) {
            //get starter rotation angle then get hot much is moved and divided by a slow factor
            angleX = (float) (anchorAngleX + (anchorY - mouseY) / (3 / MinecraftClient.getInstance().getWindow().getScaleFactor()));
            angleY = (float) (anchorAngleY - (anchorX - mouseX) / (3 / MinecraftClient.getInstance().getWindow().getScaleFactor()));

            //cap to 360, so we don't get extremely high unnecessary rotation values
            if (angleX >= 360 || angleX <= -360) {
                anchorY = (float) mouseY;
                anchorAngleX = 0;
                angleX = 0;
            }
            if (angleY >= 360 || angleY <= -360) {
                anchorX = (float) mouseX;
                anchorAngleY = 0;
                angleY = 0;
            }

            return true;
        }

        //right click - move
        else if (isDragging) {
            //get how much it should move
            //get actual pos of the mouse, then subtract starter X,Y
            float x = (float) (mouseX - dragDeltaX);
            float y = (float) (mouseY - dragDeltaY);

            //move it
            if (modelX >= 0 && modelX <= this.width)
                modelX = (int) (dragAnchorX + x);
            if (modelY >= 0 && modelY <= this.height)
                modelY = (int) (dragAnchorY + y);

            //if out of range - move it back
            //can't be "elsed" because it needs to be checked after the move
            modelX = modelX < 0 ? 0 : Math.min(modelX, this.width);
            modelY = modelY < 0 ? 0 : Math.min(modelY, this.height);

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        //scroll - scale

        //set scale direction
        float scaleDir = (amount > 0) ? SCALE_FACTOR : 1 / SCALE_FACTOR;

        //determine scale
        scaledValue = ((scale + scaledValue) * scaleDir) - scale;

        //limit scale
        if (scaledValue <= -35) scaledValue = -35.0F;
        if (scaledValue >= 250) scaledValue = 250.0F;

        return true;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }
}
