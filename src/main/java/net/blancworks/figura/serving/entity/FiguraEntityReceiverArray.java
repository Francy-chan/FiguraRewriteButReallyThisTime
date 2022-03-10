package net.blancworks.figura.serving.entity;

import net.blancworks.figura.trust.TrustContainer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class FiguraEntityReceiverArray<T extends FiguraEventReceiver> implements FiguraEventReceiver {
    public TrustContainer trustContainer;
    public T[] entries;

    @Override
    public void setTrustContainer(TrustContainer tc) {
        this.trustContainer = tc;

        if (entries == null)
            return;

        for (T child : entries)
            if (child != null)
                child.setTrustContainer(tc);
    }

    @Override
    public void tick() {
        if (entries == null)
            return;
        setTrustContainer(trustContainer);

        for (T child : entries)
            if (child != null)
                child.tick();
    }

    @Override
    public void render(Entity targetEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (entries == null)
            return;
        setTrustContainer(trustContainer);

        for (T child : entries)
            if (child != null)
                child.render(targetEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
