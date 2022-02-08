package net.blancworks.figura.modifications.mixins;

import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(at = @At("HEAD"), method = "render")
    public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        FiguraMetadataHolder holder = (FiguraMetadataHolder) entity;
        FiguraEntityMetadata metadata = holder.getFiguraMetadata();

        metadata.render(yaw, tickDelta, matrices, vertexConsumers, light);
    }
}
