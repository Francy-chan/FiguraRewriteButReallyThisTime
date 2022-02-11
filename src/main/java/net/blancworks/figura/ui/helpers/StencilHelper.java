package net.blancworks.figura.ui.helpers;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;

public class StencilHelper {
    // -- Variables -- //
    public static int currentStencilValue = 0;

    // -- Functions -- //

    /**
     * Sets the current rendering state to draw to this card's stencil ID.
     *
     * Every pixel drawn in this rendering mode is set to the stencil layer ID.
     */
    public void setupStencilWrite() {
        //Allow writing to stencil buffer
        GlStateManager._stencilMask(0xFF);

        //Stencil fail = keep (never happens)
        //Depth fail = keep
        //Both success = replace with layer ID
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

        //Always write the stencil ID for drawing prep phase.
        GlStateManager._stencilFunc(GL11.GL_ALWAYS, currentStencilValue++, 0xff);
    }

    /**
     * Sets the current rendering state to test all geometry against this card's stencil ID.
     *
     * If the pixel at a given location doesn't match the stencil ID, the pixel does not draw.
     */
    public void setupStencilTest() {
        //Turn off writing to stecil buffer, we're only testing against it here.
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GlStateManager._stencilMask(0x00);

        //Test against the stencil layer ID.
        GlStateManager._stencilFunc(GL11.GL_EQUAL, currentStencilValue, 0xff);
    }

    /**
     * Turns "off" stencil testing without actually disabling it.
     */
    public void resetStencilState() {
        //Turn off writing to stecil buffer.
        GlStateManager._stencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GlStateManager._stencilMask(0x00);

        //Always succeed in the stencil test, no matter what.
        GlStateManager._stencilFunc(GL11.GL_ALWAYS, 0, 0xff);
        currentStencilValue--;
    }
}
