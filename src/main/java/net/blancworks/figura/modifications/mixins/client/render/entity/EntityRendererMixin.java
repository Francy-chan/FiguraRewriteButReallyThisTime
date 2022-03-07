package net.blancworks.figura.modifications.mixins.client.render.entity;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.utils.TextUtils;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Shadow @Final protected EntityRenderDispatcher dispatcher;

    @Shadow public abstract TextRenderer getTextRenderer();

    @Inject(at = @At("HEAD"), method = "renderLabelIfPresent", cancellable = true)
    private void renderLabelIfPresent_HEAD(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        //get metadata
        FiguraMetadataHolder holder = (FiguraMetadataHolder) entity;
        if (holder == null)
            return;

        //check entity distance
        if (this.dispatcher.getSquaredDistanceToCamera(entity) > 4096)
            return;

        //get customization
        FiguraEntityMetadata<?> metadata = holder.getFiguraMetadata();
        NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.entityNameplate;

        //apply customizations

        //enabled
        if (custom.enabled != null && !custom.enabled) {
            ci.cancel();
            return;
        }

        matrices.push();

        //pos
        FiguraVec3 pos = FiguraVec3.get(0f, entity.getHeight() + 0.5f, 0f);
        if (custom.position != null)
            pos.add(custom.position);

        matrices.translate(pos.x, pos.y, pos.z);

        //rotation
        matrices.multiply(this.dispatcher.getRotation());

        //scale
        float scale = 0.025f;
        FiguraVec3 scaleVec = FiguraVec3.get(-scale, -scale, scale);
        if (custom.scale != null)
            scaleVec.multiply(custom.scale);

        matrices.scale((float) scaleVec.x, (float) scaleVec.y, (float) scaleVec.z);

        //text
        if (custom.text != null) {
            Text replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
            text = TextUtils.replaceInText(text, "\\b" + entity.getName().getString() + "\\b", replacement);
        }

        //render
        boolean isSneaking = entity.isSneaky();
        boolean deadmau = "deadmau5".equals(text.getString());

        float bgOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int bgColor = (int) (bgOpacity * 0xFF) << 24;

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = this.getTextRenderer();

        List<Text> textList = TextUtils.splitText(text, "\n");

        for (int i = 0; i < textList.size(); i++) {
            Text text1 = textList.get(i);
            int line = i - textList.size() + 1;

            float x = -textRenderer.getWidth(text1) / 2f;
            float y = (deadmau ? -10f : 0f) + (textRenderer.fontHeight + 1.5f) * line;

            textRenderer.draw(text1, x, y, 0x20FFFFFF, false, matrix4f, vertexConsumers, !isSneaking, bgColor, light);
            if (!isSneaking)
                textRenderer.draw(text1, x, y, -1, false, matrix4f, vertexConsumers, false, 0, light);
        }

        matrices.pop();
        ci.cancel();
    }
}
