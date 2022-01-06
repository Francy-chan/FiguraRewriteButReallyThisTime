package net.blancworks.figura.avatar;

import net.blancworks.figura.avatar.components.model.FiguraModel;
import net.blancworks.figura.avatar.components.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.components.FiguraTextureSet;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

/**
 * The most top-level part of figura avatars: The Avatar class.
 * <p>
 * Essentially just acts as a holder for avatar components, and something to relay events to those components.
 */
public class FiguraAvatar {
    private static final Identifier figura_texid = new Identifier("textures/entity/creeper/creeper.png");

    //Components of the avatar
    public FiguraModel model;
    public FiguraScriptEnvironment scriptEnv;
    public FiguraTextureSet textures;

    public <T extends Entity> void render(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int someInt) {


        //If there is an avatar, render it.

        /*RenderLayer layer = RenderLayer.getEntityCutoutNoCull(figura_texid);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(layer);

        vertexConsumer.vertex(0,0,0, );
        vertexConsumer.next();

        vertexConsumer.vertex(matrixStack.peek().getNormalMatrix(), 0,1,0);
        vertexConsumer.texture(0,1);
        vertexConsumer.next();

        vertexConsumer.vertex(matrixStack.peek().getModel(), 0,1,0);
        vertexConsumer.texture(1,0);
        vertexConsumer.next();

        vertexConsumer.vertex(matrixStack.peek().getModel(), 1,1,0);
        vertexConsumer.texture(1,1);
        vertexConsumer.next();*/

    }
}
