package net.blancworks.figura.ui.widgets.cards;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;

public class EntityCardElement<T extends LivingEntity> extends CardElement {

    public Text name;
    public Text author;
    public T entity;

    public EntityCardElement(Vec3f color, int stencilID, Text name, Text author, T entity) {
        super(color, stencilID);
        this.entity = entity;

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        this.name = TextUtils.trimToWidthEllipsis(textRenderer, name, 59); // 64 - 3 - 2
        this.author = TextUtils.trimToWidthEllipsis(textRenderer, author, 75); //64 + 64 * 0.25 - 3 - 2
    }

    @Override
    public void renderCardContent(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.renderCardContent(stack, mouseX, mouseY, delta);

        //render model
        if (entity != null) {
            RenderSystem.enableDepthTest();
            stack.push();
            stack.translate(0, 0, -15);
            UIHelper.drawEntity(32, 52, 30, 0, 0, entity, stack);
            stack.pop();
            RenderSystem.disableDepthTest();
        }
    }

    @Override
    protected void renderOverlay(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.renderOverlay(stack, mouseX, mouseY, delta);

        //render overlay
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, OVERLAY);
        UIHelper.drawTexture(stack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

        //render texts
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        //name
        if (name != null) {
            stack.push();
            stack.translate(3f, 3f, 2f); //3px offset
            UIHelper.renderOutlineText(stack, textRenderer, name, 0, 0, 0xFFFFFF, 0x303030);
            stack.pop();
        }

        //author
        if (author != null) {
            stack.push();
            stack.translate(3f, 11f, 2f); //3px offset + 7px above text + 1px spacing
            stack.scale(0.75f, 0.75f, 1f);
            UIHelper.drawWithShadow(stack, textRenderer, author.asOrderedText(), 0, 0, 0xFFFFFF);
            stack.pop();
        }
    }
}
