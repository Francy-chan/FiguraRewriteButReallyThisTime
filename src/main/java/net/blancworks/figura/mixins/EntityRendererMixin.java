package net.blancworks.figura.mixins;

import net.blancworks.figura.accessors.FiguraMetadataHolder;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.dealer.FiguraHouse;
import net.blancworks.figura.entity.FiguraEntityMetadata;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    @Inject(at = @At("HEAD"), method = "render")
    public void render(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int someInt, CallbackInfo ci) {
        FiguraMetadataHolder holder = (FiguraMetadataHolder) entity;
        FiguraEntityMetadata metadata = holder.getFiguraMetadata();

        for (int i = 0; i < FiguraHouse.dealers.length; i++) {
            try {
                var avatarFuture = metadata.avatarsByID[i];
                if (avatarFuture != null && avatarFuture.isDone()) {
                    FiguraAvatar targetAvatar = avatarFuture.get();

                    //If there is no avatar for this layer, skip.
                    if(targetAvatar == null)
                        continue;

                    targetAvatar.render(entity, f, g, matrixStack, vertexConsumerProvider, someInt);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
