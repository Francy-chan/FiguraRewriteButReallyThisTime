package net.blancworks.figura.ui.widgets.trust;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.ContextMenu;
import net.blancworks.figura.ui.widgets.lists.TrustContainerList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class PlayerElement extends AbstractContainerElement {

    private final String name;
    private final Identifier skin;
    private final ContextMenu context;

    private static final Identifier BACKGROUND = new Identifier("figura", "textures/gui/player_trust.png");

    public PlayerElement(String name, TrustContainer trust, Identifier skin, TrustContainerList parent) {
        super(40, trust, parent);
        this.name = name;
        this.skin = skin;
        this.context = new ContextMenu(this);

        generateContext();
    }

    private void generateContext() {
        //header
        context.addDivisor(new TranslatableText("figura.gui.set_trust"));

        //actions
        ArrayList<Identifier> groupList = new ArrayList<>(TrustManager.GROUPS.keySet());
        for (int i = 0; i < (TrustManager.isLocal(trust) ? groupList.size() : groupList.size() - 1); i++) {
            Identifier parentID = groupList.get(i);
            TrustContainer container = TrustManager.get(parentID);
            context.addAction(container.getGroupName().copy().setStyle(Style.EMPTY.withColor(container.getGroupColor())), button -> {
                trust.setParent(parentID);
                if (parent.getSelectedEntry() == this)
                    parent.parent.updateTrustData(trust);
            });
        }
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
        drawTexture(matrices, x, y, width, height, 0f, 0f, 174, 40, 174, 40);

        //head
        RenderSystem.setShaderTexture(0, this.skin);
        drawTexture(matrices, x + 4, y + 4, 32, 32, 8f, 8f, 8, 8, 64, 64);

        //hat
        RenderSystem.enableBlend();
        drawTexture(matrices, x + 4, y + 4, 32, 32, 40f, 8f, 8, 8, 64, 64);
        RenderSystem.disableBlend();

        //name
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        UIHelper.renderOutlineText(matrices, textRenderer, Text.of(this.name), x + 40, y + 4, 0xFFFFFF, 0);

        //uuid
        matrices.push();
        matrices.translate(x + 40, y + 4 + textRenderer.fontHeight, 0f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        drawTextWithShadow(matrices, textRenderer, Text.of(trust.name), 0, 0, 0x888888);
        matrices.pop();

        //trust
        drawTextWithShadow(matrices, textRenderer, trust.getGroupName(), x + 40, y + height - textRenderer.fontHeight - 4, trust.getGroupColor());

        matrices.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY))
            return false;

        //context menu on right click
        if (button == 1) {
            context.setPos((int) mouseX, (int) mouseY);
            context.setVisible(true);
            UIHelper.setContext(context);
            return true;
        }
        //hide old context menu
        else if (UIHelper.getContext() == context) {
            context.setVisible(false);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public String getName() {
        return name;
    }
}
