package net.blancworks.figura.ui.cards;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class CardEntity<T extends LivingEntity> extends CardElement {

    private final Text name;
    private final Text author;
    private final T entity;

    public CardEntity(Vec3f color, Text name, Text author, T entity) {
        super(color);
        this.name = name;
        this.author = author;
        this.entity = entity;
    }

    @Override
    public void renderCardContent(MatrixStack stack, int mouseX, int mouseY, float delta) {
        super.renderCardContent(stack, mouseX, mouseY, delta);

        //render model
        RenderSystem.enableDepthTest();

        stack.push();
        stack.translate(0, 0, -15);
        drawEntity(32, 52, 30, 0, 0, entity, stack);
        stack.pop();

        RenderSystem.disableDepthTest();

        //render overlay
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.DST_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderTexture(0, OVERLAY);
        drawTexture(stack, 0, 0, 64, 96, 0, 0, 64, 96, 64, 96);

        //render texts
        MinecraftClient client = MinecraftClient.getInstance();

        //name
        stack.push();
        stack.translate(3f, 3f, 2f); //3px offset
        String nameString = client.textRenderer.trimToWidth(name.getString(), 59); // 64 - 3 - 2
        drawStringWithShadow(stack, client.textRenderer, nameString, 0, 0, 0xFFFFFF);
        stack.pop();

        //author
        stack.push();
        stack.translate(3f, 11f, 2f); //3px offset + 7px above text + 1px spacing
        stack.scale(0.75f, 0.75f,1f);
        String authorString = client.textRenderer.trimToWidth(author.getString(), 75); //64 + 64 * 0.75 - 3 - 2
        drawStringWithShadow(stack, client.textRenderer, authorString, 0, 0, 0xFFFFFF);
        stack.pop();
    }

    public static void drawEntity(int x, int y, int scale, float pitch, float yaw, LivingEntity livingEntity, MatrixStack matrixStack) {
        //rotation
        float h = Float.isNaN(yaw) ? 0f : (float) Math.atan(yaw / 40f);
        float l = Float.isNaN(pitch) ? 0f : (float) Math.atan(pitch / 40f);

        //apply matrix transformers
        matrixStack.push();
        matrixStack.translate(x, y, 0);
        matrixStack.scale(1f, 1f, -1f);
        matrixStack.scale((float) scale, (float) scale, (float) scale);

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0f);
        quaternion.hamiltonProduct(quaternion2);
        matrixStack.multiply(quaternion);
        quaternion2.conjugate();

        //backup entity variables
        float bodyYaw = livingEntity.bodyYaw;
        float entityYaw = livingEntity.getYaw();
        float entityPitch = livingEntity.getPitch();
        float prevHeadYaw = livingEntity.prevHeadYaw;
        float headYaw = livingEntity.headYaw;

        //apply entity rotation
        livingEntity.bodyYaw = 180f + h * 20f;
        livingEntity.setYaw(180f + h * 40f);
        livingEntity.setPitch(-l * 20f);
        livingEntity.headYaw = livingEntity.getYaw();
        livingEntity.prevHeadYaw = livingEntity.getYaw();

        //setup entity renderer
        RenderSystem.setShaderLights(Vec3f.ZERO, Vec3f.ZERO);
        //DiffuseLighting.method_34742();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        boolean renderHitboxes = entityRenderDispatcher.shouldRenderHitboxes();
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);
        entityRenderDispatcher.setRotation(quaternion2);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        //render
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0d, -1d, 0d, 0f, 1f, matrixStack, immediate, 0xf000f0));
        immediate.draw();

        //restore entity rendering data
        entityRenderDispatcher.setRenderHitboxes(renderHitboxes);
        entityRenderDispatcher.setRenderShadows(true);

        //restore entity data
        livingEntity.bodyYaw = bodyYaw;
        livingEntity.setYaw(entityYaw);
        livingEntity.setPitch(entityPitch);
        livingEntity.prevHeadYaw = prevHeadYaw;
        livingEntity.headYaw = headYaw;

        //pop matrix
        matrixStack.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }
}
