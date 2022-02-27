package net.blancworks.figura.ui.helpers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
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

    public static void drawEntity(int x, int y, int scale, float pitch, float yaw, LivingEntity entity, MatrixStack matrices) {
        //apply matrix transformers
        matrices.push();
        matrices.translate(x, y, 0d);
        matrices.scale((float) scale, (float) scale, (float) scale);
        matrices.peek().getPositionMatrix().multiply(Matrix4f.scale(1f, 1f, -1f)); //Scale ONLY THE POSITIONS! Inverted normals don't work for whatever reason

        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180f);
        Quaternion quaternion2 = Vec3f.POSITIVE_X.getDegreesQuaternion(pitch);
        quaternion.hamiltonProduct(quaternion2);
        matrices.multiply(quaternion);
        quaternion2.conjugate();

        //backup entity variables
        float bodyYaw = entity.bodyYaw;
        float entityYaw = entity.getYaw();
        float entityPitch = entity.getPitch();
        float prevHeadYaw = entity.prevHeadYaw;
        float headYaw = entity.headYaw;
        boolean invisible = entity.isInvisible();

        //apply entity rotation
        entity.bodyYaw = 180f - yaw;
        entity.setYaw(180f - yaw);
        entity.setPitch(0f);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        entity.setInvisible(false);
        //showOwnNametag = (boolean) Config.PREVIEW_NAMEPLATE.value;
        //renderFireOverlay = false;

        //set up lighting
        DiffuseLighting.disableGuiDepthLighting();
        RenderSystem.setShaderLights(Util.make(new Vec3f(-0.2f, -1f, -1f), Vec3f::normalize), Util.make(new Vec3f(-0.2f, 0.4f, -0.3f), Vec3f::normalize));

        //setup entity renderer
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        boolean renderHitboxes = dispatcher.shouldRenderHitboxes();
        dispatcher.setRenderHitboxes(false);
        dispatcher.setRenderShadows(false);
        dispatcher.setRotation(quaternion2);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        //render
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0d, -1d, 0d, 0f, 1f, matrices, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE));
        immediate.draw();

        //restore entity rendering data
        dispatcher.setRenderHitboxes(renderHitboxes);
        dispatcher.setRenderShadows(true);

        //restore entity data
        entity.bodyYaw = bodyYaw;
        entity.setYaw(entityYaw);
        entity.setPitch(entityPitch);
        entity.prevHeadYaw = prevHeadYaw;
        entity.headYaw = headYaw;
        entity.setInvisible(invisible);
        //showOwnNametag = false;
        //renderFireOverlay = true;

        //pop matrix
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
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
        return isMouseOver(widget.x, widget.y, widget.getWidth(), widget.getHeight(), mouseX, mouseY);
    }

    public static boolean isMouseOver(int x, int y, int width, int height, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
