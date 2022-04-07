package net.blancworks.figura.ui.widgets.trust;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.lists.TrustContainerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class GroupElement extends AbstractContainerElement {

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/group_trust.png");
    private boolean enabled;

    public GroupElement(TrustContainer container, TrustContainerList parent) {
        super(20, container, parent);
        this.enabled = container.visible;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(x + width / 2f, y + height / 2f, 100);
        matrices.scale(scale, scale, scale);

        animate(mouseX, mouseY, delta);

        //fix x, y
        int x = -width / 2;
        int y = -height / 2;

        //selected overlay
        if (this.parent.getSelectedEntry() == this) {
            UIHelper.fillRounded(matrices, x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF);
        }

        //background
        RenderSystem.setShaderTexture(0, BACKGROUND);
        drawTexture(matrices, x, y, width, height, 0f, enabled ? 20f : 0f, 174, 20, 174, 40);

        //name
        Text text = trust.getGroupName();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        UIHelper.renderOutlineText(matrices, textRenderer, text, x + width / 2f - textRenderer.getWidth(text) / 2f, y + height / 2f - textRenderer.fontHeight / 2f, trust.getGroupColor(), 0);

        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return this.isMouseOver(mouseX, mouseY) && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        if (parent.selectedEntry == this) {
            enabled = !enabled;
            trust.visible = enabled;

            parent.updateScroll();
        }

        super.onPress();
    }

    @Override
    public boolean isVisible() {
        return true;
    }
}
