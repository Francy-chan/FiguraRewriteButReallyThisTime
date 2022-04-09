package net.blancworks.figura.modifications.mixins.client.render.entity;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.config.Config;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.utils.TextUtils;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void renderFiguraLabelIfPresent(AbstractClientPlayerEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        //config and distance check
        if (!(boolean) Config.NAMEPLATE_MODIFICATIONS.value || this.dispatcher.getSquaredDistanceToCamera(entity) > 4096)
            return;

        //get data
        UUID uuid = entity.getUuid();
        FiguraMetadata metadata = FiguraHouse.getMetadata(uuid);

        //get customizations
        NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.entityNameplate;

        //enabled
        if (custom.enabled != null && !custom.enabled) {
            ci.cancel();
            return;
        }

        //trust check
        boolean trust = metadata.trustContainer.get(TrustContainer.Trust.NAMEPLATE_EDIT) == 1;

        matrices.push();

        //pos
        FiguraVec3 pos = FiguraVec3.get(0f, entity.getHeight() + 0.5f, 0f);
        if (custom.position != null && trust)
            pos.add(custom.position);

        matrices.translate(pos.x, pos.y, pos.z);

        //rotation
        matrices.multiply(this.dispatcher.getRotation());

        //scale
        float scale = 0.025f;
        FiguraVec3 scaleVec = FiguraVec3.get(-scale, -scale, scale);
        if (custom.scale != null && trust)
            scaleVec.multiply(custom.scale);

        matrices.scale((float) scaleVec.x, (float) scaleVec.y, (float) scaleVec.z);

        //text
        Text replacement;
        if (custom.text != null && trust) {
            replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
        } else {
            replacement = new LiteralText(entity.getName().getString());
        }

        //badges
        if ((boolean) Config.BADGES.value) {
            Text badges = NameplateCustomizations.fetchBadges(metadata);
            if (badges != null) ((MutableText) replacement).append(badges);
        }

        //apply text
        text = TextUtils.replaceInText(text, "\\b" + entity.getName().getString() + "\\b", replacement);

        // * variables * //
        boolean isSneaking = entity.isSneaky();
        boolean deadmau = "deadmau5".equals(text.getString());

        float bgOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int bgColor = (int) (bgOpacity * 0xFF) << 24;

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        TextRenderer textRenderer = this.getTextRenderer();

        //render scoreboard
        boolean hasScore = false;
        if (this.dispatcher.getSquaredDistanceToCamera(entity) < 100.0D) {
            //get scoreboard
            Scoreboard scoreboard = entity.getScoreboard();
            ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
            if (scoreboardObjective != null) {
                hasScore = true;

                //render scoreboard
                ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(entity.getEntityName(), scoreboardObjective);

                Text text1 = new LiteralText(Integer.toString(scoreboardPlayerScore.getScore())).append(" ").append(scoreboardObjective.getDisplayName());
                float x = -textRenderer.getWidth(text1) / 2f;
                float y = deadmau ? -10f : 0f;

                textRenderer.draw(text1, x, y, 0x20FFFFFF, false, matrix4f, vertexConsumers, !isSneaking, bgColor, light);
                if (!isSneaking)
                    textRenderer.draw(text1, x, y, -1, false, matrix4f, vertexConsumers, false, 0, light);
            }
        }

        //render name
        List<Text> textList = TextUtils.splitText(text, "\n");

        for (int i = 0; i < textList.size(); i++) {
            Text text1 = textList.get(i);
            int line = i - textList.size() + (hasScore ? 0 : 1);

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
