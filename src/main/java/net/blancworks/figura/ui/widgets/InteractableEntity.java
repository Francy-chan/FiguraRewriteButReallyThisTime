package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;

public class InteractableEntity extends ClickableWidget implements Element, Selectable {

    //constructor data
    private final LivingEntity entity;
    private final float pitch, yaw, scale;
    private final int x, y;

    //transformation data
    private boolean isRotating = false, isDragging = false;

    private float anchorX = 0f, anchorY = 0f;
    private float anchorAngleX = 0f, anchorAngleY = 0f;
    private float angleX, angleY;

    private float scaledValue = 0f;
    private static final float SCALE_FACTOR = 1.1F;

    private int modelX = 0, modelY = 0;
    private float dragDeltaX, dragDeltaY;
    private float dragAnchorX, dragAnchorY;

    public InteractableEntity(int x, int y, int width, int height, int scale, float pitch, float yaw, LivingEntity entity) {
        super(0, 0, width, height, LiteralText.EMPTY);
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.pitch = pitch;
        this.yaw = yaw;
        this.entity = entity;

        angleX = pitch;
        angleY = yaw;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0f, 0f, -400f);
        UIHelper.drawEntity(modelX + x, modelY + y, (int) (scale + scaledValue), angleX, angleY, entity, matrices);
        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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
                modelX = 0;
                modelY = 0;
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
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
        //set rotations
        if (isRotating) {
            //get starter rotation angle then get hot much is moved and divided by a slow factor
            angleX = (float) (anchorAngleX + (anchorY - mouseY) / (3 / MinecraftClient.getInstance().getWindow().getScaleFactor()));
            angleY = (float) (anchorAngleY - (anchorX - mouseX) / (3 / MinecraftClient.getInstance().getWindow().getScaleFactor()));

            //prevent rating so much down and up
            if (angleX > 90) {
                anchorY = (float) mouseY;
                anchorAngleX = 90;
                angleX = 90;
            } else if (angleX < -90) {
                anchorY = (float) mouseY;
                anchorAngleX = -90;
                angleX = -90;
            }
            //cap to 360, so we don't get extremely high unnecessary rotation values
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

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        //scroll - scale

        //set scale direction
        float scaledir = (amount > 0) ? SCALE_FACTOR : 1 / SCALE_FACTOR;

        //determine scale
        scaledValue = ((scale + scaledValue) * scaledir) - scale;

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
}
