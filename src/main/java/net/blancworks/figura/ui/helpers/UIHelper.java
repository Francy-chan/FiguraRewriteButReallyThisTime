package net.blancworks.figura.ui.helpers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class UIHelper {
    // -- Variables -- //

    //Used for GUI rendering
    private static final CustomFramebuffer figuraFramebuffer = new CustomFramebuffer();

    private static int previousFBO = -1;

    // -- Functions -- //

    public static void useFiguraGuiFramebuffer() {
        previousFBO = GL30.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);


        int windowWidth = MinecraftClient.getInstance().getWindow().getWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        figuraFramebuffer.setSize(windowWidth, windowHeight);

        //Enable stencil buffer during this phase of rendering
        GL30.glEnable(GL30.GL_STENCIL_TEST);
        GlStateManager._stencilMask(0xFF);
        //Bind custom GUI framebuffer to be used for rendering
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, figuraFramebuffer.getFbo());

        //Clear GUI framebuffer
        GlStateManager._clearStencil(0);
        GlStateManager._clearColor(0f, 0f, 0f, 1f);
        GlStateManager._clearDepth(1);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT, false);

        Matrix4f mf = RenderSystem.getProjectionMatrix();
        MinecraftClient.getInstance().getFramebuffer().draw(windowWidth, windowHeight, false);
        RenderSystem.setProjectionMatrix(mf);
    }

    public static void useVanillaFramebuffer(MatrixStack stack) {
        //Reset state before we go back to normal rendering
        GlStateManager._enableDepthTest();
        //Set a sensible default for stencil buffer operations
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, 0xFF);
        GL30.glDisable(GL30.GL_STENCIL_TEST);

        //Bind vanilla framebuffer again
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, previousFBO);

        RenderSystem.disableBlend();
        //Draw GUI framebuffer -> vanilla framebuffer
        int windowWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getScaledHeight();

        Matrix4f mf = RenderSystem.getProjectionMatrix();
        figuraFramebuffer.drawToScreen(stack, windowWidth, windowHeight);
        RenderSystem.setProjectionMatrix(mf);
        RenderSystem.enableBlend();
    }


    public static void drawEntity(int x, int y, int scale, float pitch, float yaw, LivingEntity livingEntity, MatrixStack matrixStack) {
        //rotation
        float h = Float.isNaN(yaw) ? 0f : (float) Math.atan(yaw / 40f);
        float l = Float.isNaN(pitch) ? 0f : (float) Math.atan(pitch / 40f);

        //apply matrix transformers
        matrixStack.push();
        matrixStack.translate(x,y,0);
        matrixStack.scale(1,1,1);
        matrixStack.scale((float) scale, (float) scale, (float)scale);
        matrixStack.peek().getPositionMatrix().multiply(Matrix4f.scale(1,1,-1)); //Scale ONLY THE POSITIONS! Inverted normals don't work for whatever reason

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(0);
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

        //set up lighting
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.setShaderLights(Util.make(new Vec3f(-0.2f, -1.0f, -1.0f), Vec3f::normalize), Util.make(new Vec3f(-0.2f, 0.4f, -0.3f), Vec3f::normalize));

        //setup entity renderer
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        boolean renderHitboxes = entityRenderDispatcher.shouldRenderHitboxes();
        entityRenderDispatcher.setRenderHitboxes(false);
        entityRenderDispatcher.setRenderShadows(false);
        //entityRenderDispatcher.setRotation(quaternion2);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        //render
        RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(livingEntity, 0d, -1d, 0d, 0f, 1f, matrixStack, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE));
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

        RenderSystem.applyModelViewMatrix();
    }

    public static void renderBackgroundTexture(int width, int height, Identifier texture) {
        RenderSystem.setShaderTexture(0, texture);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(0f, height, 0f).texture(0f, height / 32f + 0f).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(width, height, 0f).texture(width / 32f, height / 32f + 0f).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(width, 0f, 0f).texture(width / 32f, 0f).color(255, 255, 255, 255).next();
        bufferBuilder.vertex(0f, 0f, 0f).texture(0f, 0f).color(255, 255, 255, 255).next();
        tessellator.draw();
    }

    //widget.isMouseOver() returns false if the widget is disabled or invisible
    public static boolean isMouseOver(ClickableWidget widget, double mouseX, double mouseY) {
        int x = widget.x;
        int y = widget.y;

        int width = widget.getWidth();
        int height = widget.getHeight();

        return isMouseOver(x, y, width, height, mouseX, mouseY);
    }

    public static boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
