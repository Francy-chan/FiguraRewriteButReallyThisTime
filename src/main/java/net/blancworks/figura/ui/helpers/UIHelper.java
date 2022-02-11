package net.blancworks.figura.ui.helpers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class UIHelper {
    // -- Variables -- //

    //Used for GUI rendering
    private static final CustomFramebuffer figuraFramebuffer = new CustomFramebuffer();
    private static MatrixStack stack;

    // -- Functions -- //


    public static void useFiguraGuiFramebuffer(MatrixStack stack){
        UIHelper.stack = stack;
        int windowWidth = MinecraftClient.getInstance().getWindow().getWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        figuraFramebuffer.setSize(windowWidth, windowHeight);

        //Enable stencil buffer during this phase of rendering
        GL30.glEnable(GL30.GL_STENCIL_TEST);
        GlStateManager._stencilMask(0xff);
        //Bind custom GUI framebuffer to be used for rendering
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, figuraFramebuffer.getFbo());

        //Clear GUI framebuffer
        GlStateManager._clearStencil(0);
        GlStateManager._clearColor(0, 0, 0, 1.0F);
        GlStateManager._clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT, false);

        RenderSystem.backupProjectionMatrix();
        MinecraftClient.getInstance().getFramebuffer().draw(windowWidth, windowHeight, false);
        RenderSystem.restoreProjectionMatrix();
    }

    public static void useVanillaFramebuffer(){
        //Reset state before we go back to normal rendering
        GlStateManager._enableDepthTest();
        //Set a sensible default for stencil buffer operations
        GlStateManager._stencilFunc(GL11.GL_EQUAL, 0, 255);
        GL30.glDisable(GL30.GL_STENCIL_TEST);

        //Bind vanilla framebuffer again
        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, MinecraftClient.getInstance().getFramebuffer().fbo);

        RenderSystem.disableBlend();
        //Draw GUI framebuffer -> vanilla framebuffer
        int windowWidth = MinecraftClient.getInstance().getWindow().getWidth();
        int windowHeight = MinecraftClient.getInstance().getWindow().getHeight();
        figuraFramebuffer.drawToScreen(stack, windowWidth, windowHeight);
        RenderSystem.enableBlend();
    }

}
