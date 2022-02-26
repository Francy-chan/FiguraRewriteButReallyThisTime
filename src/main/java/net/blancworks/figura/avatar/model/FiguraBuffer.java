package net.blancworks.figura.avatar.model;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.avatar.texture.FiguraTexture;
import net.blancworks.figura.math.matrix.FiguraMat3;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.blancworks.figura.math.vector.FiguraVec4;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.*;

public class FiguraBuffer {

    private Queue<FiguraVertex> transformedVerts;
    private final FiguraVertex[] untransformedVerts;
    private int curIndex;

    private final FiguraTexture mainTex;
    private final FiguraTexture emissiveTex;

    private static final ImmutableSet<String> supportedRenderModes = new ImmutableSet.Builder<String>().add(
            "cutout", "cutout_cull",
            "solid",
            "translucent", "translucent_cull",
            "glint", "end"
    ).build();
    private final Map<String, RenderLayer> mainRenderLayers = new HashMap<>();
    private final Map<String, RenderLayer> emissiveRenderLayers = new HashMap<>();

    private RenderLayer currentMainLayer;
    private RenderLayer currentEmissiveLayer;

    private FiguraMat4 cachedPositionMatrix;
    private FiguraMat3 cachedNormalMatrix;

    private boolean texturesUploaded = false;

    public FiguraBuffer(FiguraTexture mainTex, FiguraTexture emissiveTex, List<FiguraVertex> vertexList) {
        untransformedVerts = vertexList.toArray(new FiguraVertex[0]);
        transformedVerts = new LinkedList<>();
        this.mainTex = mainTex;
        this.emissiveTex = emissiveTex;
    }

    public void uploadTexturesIfNeeded() {
        if (texturesUploaded) return;

        if(mainTex != null)
            mainTex.registerAndUpload();

        if(emissiveTex != null)
            emissiveTex.registerAndUpload();

        texturesUploaded = true;
    }

    public RenderLayer getMainLayer(String renderMode) {
        return switch (renderMode) {
            default -> RenderLayer.getEntityCutoutNoCull(mainTex.textureID);
            case "cutout_cull" -> RenderLayer.getEntityCutout(mainTex.textureID);
            case "solid" -> RenderLayer.getEntitySolid(mainTex.textureID);
            case "translucent" -> RenderLayer.getEntityTranslucent(mainTex.textureID);
            case "translucent_cull" -> RenderLayer.getEntityTranslucentCull(mainTex.textureID);
            case "end" -> RenderLayer.getEndPortal();
        };
    }

    public RenderLayer getEmissiveLayer(String renderMode) {
        return RenderLayer.getEyes(emissiveTex.textureID);
    }

    public void setModifications(FiguraMat4 posMat, FiguraMat3 normalMat, String renderMode) {
        cachedPositionMatrix = posMat;
        cachedNormalMatrix = normalMat;

        if (!supportedRenderModes.contains(renderMode))
            renderMode = "cutout";


        if(mainTex == null)
            currentMainLayer = null;
        else
            currentMainLayer = mainRenderLayers.computeIfAbsent(renderMode, this::getMainLayer);

        if(emissiveTex == null)
            currentEmissiveLayer = null;
        else
            currentEmissiveLayer = emissiveRenderLayers.computeIfAbsent(renderMode, this::getEmissiveLayer);
    }

    public void submitToVertexConsumers(VertexConsumerProvider vcp, int light, int overlay) {


        for (FiguraVertex vertex : transformedVerts) {
            if (vertex.mainLayer != null)
                submitToConsumer(vertex, vcp.getBuffer(vertex.mainLayer), light, overlay);
        }

        for (FiguraVertex vertex : transformedVerts) {
            if (vertex.emissiveLayer != null)
                submitToConsumer(vertex, vcp.getBuffer(vertex.emissiveLayer), light, overlay);
        }
    }

    private void submitToConsumer(FiguraVertex vertex, VertexConsumer consumer, int light, int overlay) {
        consumer.vertex(
                vertex.x, vertex.y, vertex.z,
                vertex.r, vertex.g, vertex.b, vertex.a,
                vertex.u, vertex.v,
                overlay,
                light,
                vertex.nx, vertex.ny, vertex.nz
        );
    }

    public void pushVertex() {
        FiguraVertex untransformed = untransformedVerts[curIndex++];
        transformedVerts.offer(untransformed.transformed(cachedPositionMatrix, cachedNormalMatrix, currentMainLayer, currentEmissiveLayer));
    }

    public void clear() {
        for (FiguraVertex v : transformedVerts)
            v.free();
        transformedVerts.clear();
        curIndex = 0;
    }

    /**
     * Destroys the textures
     */
    public void close() {
        mainTex.close();
        emissiveTex.close();
    }

    public static class FiguraVertex {

        private static final Queue<FiguraVertex> pool = new LinkedList<>();

        private FiguraVertex() {
        }

        public static FiguraVertex get(float x, float y, float z, float v, float v1, float x1, float y1, float z1) {
            return get(x, y, z, v, v1, x1, y1, z1, null, null);
        }

        public static FiguraVertex get(float x, float y, float z, float u, float v, float nx, float ny, float nz, RenderLayer mainLayer, RenderLayer emissiveLayer) {
            FiguraVertex result = pool.poll();
            if (result == null)
                result = new FiguraVertex();
            result.x = x;
            result.y = y;
            result.z = z;
            result.u = u;
            result.v = v;
            result.nx = nx;
            result.ny = ny;
            result.nz = nz;
            result.r = 1;
            result.g = 1;
            result.b = 1;
            result.a = 1;

            result.mainLayer = mainLayer;
            result.emissiveLayer = emissiveLayer;
            return result;
        }

        private static final FiguraVec4 tempPos = FiguraVec4.get();
        private static final FiguraVec3 tempNormal = FiguraVec3.get();


        public FiguraVertex free() {
            pool.add(this);
            return this;
        }

        public FiguraVertex transformed(FiguraMat4 positionMatrix, FiguraMat3 normalMatrix, RenderLayer main, RenderLayer emissive) {
            tempPos.x = x;
            tempPos.y = y;
            tempPos.z = z;
            tempPos.w = 1;
            tempPos.multiply(positionMatrix);

            tempNormal.x = nx;
            tempNormal.y = ny;
            tempNormal.z = nz;
            tempNormal.multiply(normalMatrix);

            return get((float) tempPos.x, (float) tempPos.y, (float) tempPos.z, u, v, (float) tempNormal.x, (float) tempNormal.y, (float) tempNormal.z, main, emissive);
        }

        private float x, y, z, u, v, nx, ny, nz, r, g, b, a;
        private RenderLayer mainLayer, emissiveLayer; // Target render layer this vertex wants to occupy
    }

}
