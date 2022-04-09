package net.blancworks.figura.modifications.mixins.client.render.entity;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, CallbackInfo ci) {
        if (UIHelper.forceNoFire) ci.cancel();
    }
}
