package net.blancworks.figura.ui.panels;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.FiguraDrawable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

import java.nio.file.Path;
import java.util.List;

/**
 * Slightly-extended Screen class for minecraft that allows for child screens
 */
public class Panel extends Screen implements Selectable, FiguraDrawable {

    private Screen childScreen;
    public int x, y;
    private boolean visible = true;

    protected Panel(int x, int y, int width, int height, Text title) {
        super(title);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected Panel(Text title) {
        super(title);
    }

    public void setChildScreen(Screen newScreen) {
        if (childScreen != null)
            childScreen.close();

        childScreen = newScreen;
        if (newScreen != null) {
            newScreen.init(MinecraftClient.getInstance(), width, height);
        }
    }

    public Screen getChildScreen() {
        return childScreen;
    }

    public void renderBackground() {
        Vec2f screen = new Vec2f(MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
        UIHelper.renderBackgroundTexture((int) screen.x, (int) screen.y, getBackground());
    }

    public Identifier getBackground() {
        return OPTIONS_BACKGROUND_TEXTURE;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public void tick() {
        if (childScreen != null) childScreen.tick();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (childScreen != null)
            childScreen.render(matrices, mouseX, mouseY, delta);

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (childScreen != null) childScreen.close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return (childScreen != null && childScreen.keyPressed(keyCode, scanCode, modifiers)) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return (childScreen != null && childScreen.keyReleased(keyCode, scanCode, modifiers)) || super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //click should only work if inside the node
        return (childScreen != null && childScreen.mouseClicked(mouseX, mouseY, button)) || (this.isMouseOver(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        //yeet mouse 0 and isDragging check
        return (childScreen != null && childScreen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) || (this.getFocused() != null && this.getFocused().mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        //better check for mouse released when outside node's boundaries
        return (childScreen != null && childScreen.mouseReleased(mouseX, mouseY, button)) || (this.getFocused() != null && this.getFocused().mouseReleased(mouseX, mouseY, button));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return (childScreen != null && childScreen.mouseScrolled(mouseX, mouseY, amount)) || super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        //proper mouse over check
        return (childScreen != null && childScreen.isMouseOver(mouseX, mouseY)) || UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (childScreen != null) childScreen.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    protected void clearChildren() {
        if (childScreen != null) childScreen.close();
        childScreen = null;

        super.clearChildren();
    }

    @Override
    public void filesDragged(List<Path> paths) {
        if (childScreen != null) childScreen.filesDragged(paths);

        super.filesDragged(paths);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (childScreen != null) childScreen.resize(client, width, height);

        super.resize(client, width, height);
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
}
