package net.blancworks.figura.avatar.rendering;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.texture.FiguraTextureGroupManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Holds the state for rendering! Comes with tons of useful utility functions, too.
 */
public class FiguraRenderingState<T extends Entity> {

    // -- Render Layer map -- //
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

    // -- References -- //
    public FiguraAvatar avatar;
    public FiguraTextureGroupManager textureGroupManager; // Used to provide and access texture groups.
    public FiguraTextureGroupManager.FiguraTextureGroup currentTextureGroup; // The currently-set texture group.
    public int currentTextureGroupID = -1;

    // -- Vertex Buffers -- //
    private static final HashMap<String, FiguraVertexBuffer> buffers = new HashMap<>();
    private FiguraVertexBuffer currentBuffer;

    // -- Functions -- //

    public void setRenderMode(String mode) {
        this.renderMode = mode;
        updateTargetBuffer();
    }

    public void setTextureGroup(int id) {
        if (textureGroupManager == null) return;
        if (id < 0 || id > textureGroupManager.sets.size()) return;

        //Set up current texture group for UV calculations
        currentTextureGroup = textureGroupManager.sets.get(id);
        currentTextureGroupID = id;
        updateTargetBuffer();
    }

    private void updateTargetBuffer() {
        String targetBuffer = renderMode + currentTextureGroupID;

        currentBuffer = buffers.computeIfAbsent(targetBuffer, (s) -> {
            FiguraVertexBuffer nB = new FiguraVertexBuffer();
            nB.renderingMode = renderMode;
            nB.textureGroup = currentTextureGroupID;
            return nB;
        });
    }

    public void vertex(Vector4f pos, Vec3f normal, float r, float g, float b, float a, float u, float v) {
        currentBuffer.addVertex(
                pos, normal,
                r, g, b, a,
                u / currentTextureGroup.main.getWidth(), v / currentTextureGroup.main.getHeight(),
                OverlayTexture.DEFAULT_UV, light
        );
    }

    public void draw() {
        for (Map.Entry<String, FiguraVertexBuffer> entry : buffers.entrySet()) {
            FiguraVertexBuffer buffer = entry.getValue();

            //Skip empty buffers
            if (buffer.vertexList.size() == 0) continue;

            //Get texture group
            currentTextureGroup = textureGroupManager.sets.get(buffer.textureGroup);
            currentTextureGroup.use(avatar);

            //Render main
            {
                //Get render layer & vertex consumer based on buffer
                RenderLayer layer = layerSelectors.get(buffer.renderingMode).apply(this);
                VertexConsumer consumer = vertexConsumerProvider.getBuffer(layer);

                //Submit buffer to vertex consumer
                buffer.submitToVertexConsumer(consumer);
            }

            //Render emissive
            if (currentTextureGroup.emissive != null) {
                //Get eyes layer
                RenderLayer layer = RenderLayer.getEyes(currentTextureGroup.emissive.textureID);
                VertexConsumer consumer = vertexConsumerProvider.getBuffer(layer);

                //Submit buffer to vertex consumer
                buffer.submitToVertexConsumer(consumer);
            }

            //Clear buffer, as we're done with it now.
            //Puts all the vertices that were in this buffer in the cache, for use later
            buffer.clear();
        }
    }
}
