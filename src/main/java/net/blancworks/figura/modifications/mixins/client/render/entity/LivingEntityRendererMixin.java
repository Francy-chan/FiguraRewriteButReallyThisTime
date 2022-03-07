package net.blancworks.figura.modifications.mixins.client.render.entity;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.model.vanilla.VanillaModelDataManager;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.utils.RenderingUtils;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Shadow
    protected M model;


    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Shadow protected abstract void scale(T entity, MatrixStack matrices, float amount);

    @Shadow protected abstract void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta);

    @Shadow protected abstract float getAnimationProgress(T entity, float tickDelta);


    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render_HEAD(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        FiguraMetadataHolder holder = (FiguraMetadataHolder) entity;
        FiguraEntityMetadata<?> metadata = holder.getFiguraMetadata();

        metadata.entityFinalCustomizations.copyFromMetadata(metadata);

        try {
            PlayerEntityModel<?> pem = (PlayerEntityModel<?>) model;
            if (pem != null)
                metadata.entityFinalCustomizations.vanillaAvatarCustomizations.apply(pem);
        } catch (Exception e){

        }

        RenderingUtils.currentEntityMetadata = metadata;
    }

    @Inject(at = @At("RETURN"), method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
    public void render_RETURN(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        matrices.push();

        this.scale(entity, matrices, tickDelta);
        this.setupTransforms(entity, matrices, getAnimationProgress(entity, tickDelta), MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw), tickDelta);

        try {
            RenderingUtils.vanillaModelData = VanillaModelDataManager.getModelData(model);

            if (RenderingUtils.overrideMetadata != null) {
                RenderingUtils.overrideMetadata.targetEntity = entity;
                RenderingUtils.overrideMetadata.render(yaw, tickDelta, matrices, vertexConsumers, light);
                RenderingUtils.overrideMetadata = null;
            } else {
                FiguraMetadataHolder holder = (FiguraMetadataHolder) entity;
                FiguraEntityMetadata<?> metadata = holder.getFiguraMetadata();

                metadata.render(yaw, tickDelta, matrices, vertexConsumers, light);

                try {
                    PlayerEntityModel<?> pem = (PlayerEntityModel<?>) model;
                    if (pem != null)
                        metadata.entityFinalCustomizations.vanillaAvatarCustomizations.revert(pem);
                } catch (Exception e){

                }
            }
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
        matrices.pop();

        //Reset this just to be sure.
        RenderingUtils.currentEntityMetadata = null;
        RenderingUtils.vanillaModelData = null;
    }
}
