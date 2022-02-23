package net.blancworks.figura.ui.panels;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.List;

/**
 * Slightly-extended Screen class for minecraft that allows for child screens
 */
public class Panel extends Screen implements Selectable {
    private Screen childScreen;

    public float x, y;



    protected Panel(int x, int y, int width, int height, Text title){
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
        if (childScreen != null) childScreen.onClose();
        childScreen = newScreen;
        newScreen.init(MinecraftClient.getInstance(), width, height);
    }

    public Screen getChildScreen() {
        return childScreen;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (childScreen != null)
            childScreen.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        if (childScreen != null)
            childScreen.tick();

        super.tick();
    }

    @Override
    public void onClose() {
        if (childScreen != null)
            childScreen.onClose();
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
        return (childScreen != null && childScreen.mouseClicked(mouseX, mouseY, button)) || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return (childScreen != null && childScreen.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return (childScreen != null && childScreen.mouseReleased(mouseX, mouseY, button)) || super.mouseReleased(mouseX, mouseY, button);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return (childScreen != null && childScreen.mouseScrolled(mouseX, mouseY, amount)) || super.mouseScrolled(mouseX, mouseY, amount);

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return (childScreen != null && childScreen.isMouseOver(mouseX, mouseY)) || super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (childScreen != null) childScreen.mouseMoved(mouseX, mouseY);

        super.mouseMoved(mouseX, mouseY);
    }


    @Override
    protected void clearChildren() {
        if (childScreen != null) childScreen.onClose();
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
