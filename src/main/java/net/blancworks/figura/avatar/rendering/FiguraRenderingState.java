package net.blancworks.figura.avatar.rendering;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.texture.FiguraTextureGroupManager;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Holds the state for rendering! Comes with tons of useful utility functions, too.
 */
public class FiguraRenderingState<T extends Entity> {
    // -- Render layer map -- //
    public static final ImmutableMap<String, Function<FiguraRenderingState<?>, RenderLayer>> layerSelectors = new ImmutableMap.Builder<String, Function<FiguraRenderingState<?>, RenderLayer>>()
            .put("", figuraRenderingState -> RenderLayer.getEntityCutoutNoCull(figuraRenderingState.currentTextureGroup.main.textureID))
            .put("CUTOUT_NO_CULL", figuraRenderingState -> RenderLayer.getEntityCutoutNoCull(figuraRenderingState.currentTextureGroup.main.textureID))
            .build();

    // -- State -- //

    public T entity; //Entity we're rendering
    public float yaw; //Horizontal rotation of the entity
    public float tickDelta; //Tick delta!
    public int light; //Light level of the entity.
    public MatrixStack poseStack; //The stack for matrix transformations. Starts with the entity's position.

    public String renderMode;
    public VertexConsumerProvider vertexConsumerProvider; //Used for layers and stuff.
    public VertexConsumer currentVertexConsumer;
    private final List<VertexConsumer> allConsumers = new ArrayList<>();

    // -- References -- //
    public FiguraAvatar avatar;
    public FiguraTextureGroupManager textureGroupManager; // Used to provide and access texture groups.
    public FiguraTextureGroupManager.FiguraTextureGroup currentTextureGroup; // The currently-set texture group.


    public void setRenderMode(String mode) {
        this.renderMode = mode;
        updateVertexConsumer();
    }

    public void setTextureGroup(int id) {
        if (textureGroupManager == null) return;
        if (id < 0 || id > textureGroupManager.sets.size()) return;

        currentTextureGroup = textureGroupManager.sets.get(id);
        currentTextureGroup.use(avatar);
        updateVertexConsumer();
    }

    private void updateVertexConsumer() {
        var layerProvider = layerSelectors.get(renderMode);
        if (layerProvider == null) layerProvider = layerSelectors.get("");

        try {
            allConsumers.clear();

            //Add main
            allConsumers.add(vertexConsumerProvider.getBuffer(layerProvider.apply(this)));

            //Add emissive
            if (currentTextureGroup.emissive != null) {
                allConsumers.add(vertexConsumerProvider.getBuffer(RenderLayer.getEyes(currentTextureGroup.emissive.textureID)));
            }

            VertexConsumer consumer = allConsumers.get(0);

            for(int i = 1; i <allConsumers.size(); i++){
                consumer = VertexConsumers.union(consumer, allConsumers.get(i));
            }

            currentVertexConsumer = consumer;
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void vertex(Vector4f pos, Vec3f normal, float r, float g, float b, float a, float u, float v) {
        currentVertexConsumer.vertex(
                pos.getX(), pos.getY(), pos.getZ(),
                r, g, b, a,
                u / currentTextureGroup.main.getWidth(), v / currentTextureGroup.main.getHeight(),
                OverlayTexture.DEFAULT_UV, light,
                normal.getX(), normal.getY(), normal.getZ()
        );
    }
}
