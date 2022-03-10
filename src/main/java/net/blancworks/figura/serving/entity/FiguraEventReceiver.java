package net.blancworks.figura.serving.entity;

import net.blancworks.figura.trust.TrustContainer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface FiguraEventReceiver {

    void setTrustContainer(TrustContainer tc);

    void tick();

    void render(Entity targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light);
}
