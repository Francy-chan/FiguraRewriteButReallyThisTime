package net.blancworks.figura.avatar.newavatar;

import net.blancworks.figura.avatar.components.texture.FiguraTexture;
import net.blancworks.figura.math.matrix.FiguraMat3;
import net.blancworks.figura.math.matrix.FiguraMat4;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.blancworks.figura.math.vector.FiguraVec4;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FiguraBuffer {

    private Queue<FiguraVertex> transformedVerts;
    private final FiguraVertex[] untransformedVerts;
    private int curIndex;

    private final FiguraTexture mainTex;
    private final FiguraTexture emissiveTex;

    private RenderLayer mainLayer;
    private RenderLayer emissiveLayer;

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
        if (mainTex != null) {
            mainTex.registerAndUpload();
            mainLayer = RenderLayer.getEntityCutoutNoCull(mainTex.textureID);
        }
        if (emissiveTex != null) {
            emissiveTex.registerAndUpload();
            emissiveLayer = RenderLayer.getEntityCutoutNoCull(emissiveTex.textureID);
        }
        texturesUploaded = true;
    }

    public void setMatrices(FiguraMat4 posMat, FiguraMat3 normalMat) {
        cachedPositionMatrix = posMat;
        cachedNormalMatrix = normalMat;
    }

    public void submitToVertexConsumers(VertexConsumerProvider vcp, int light, int overlay) {
        if (mainLayer != null) {
            VertexConsumer consumer = vcp.getBuffer(mainLayer);
            for (FiguraVertex vertex : transformedVerts) {
                consumer.vertex(
                        vertex.x, vertex.y, vertex.z,
                        vertex.r, vertex.g, vertex.b, vertex.a,
                        vertex.u, vertex.v,
                        overlay,
                        light,
                        vertex.nx, vertex.ny, vertex.nz
                );
            }
        }
        if (emissiveLayer != null) {
            VertexConsumer consumer = vcp.getBuffer(emissiveLayer);
            for (FiguraVertex vertex : transformedVerts) {
                consumer.vertex(
                        vertex.x, vertex.y, vertex.z,
                        vertex.r, vertex.g, vertex.b, vertex.a,
                        vertex.u, vertex.v,
                        overlay,
                        light,
                        vertex.nx, vertex.ny, vertex.nz
                );
            }
        }
    }

    public void pushVertex() {
        FiguraVertex untransformed = untransformedVerts[curIndex++];
        transformedVerts.offer(untransformed.transformed(cachedPositionMatrix, cachedNormalMatrix));
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
    public void destroy() {
        mainTex.destroy();
        emissiveTex.destroy();
    }

    public static class FiguraVertex {

        private static final Queue<FiguraVertex> pool = new LinkedList<>();
        private FiguraVertex() {}
        public static FiguraVertex get(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
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
            return result;
        }

        private static final FiguraVec4 tempPos = FiguraVec4.get();
        private static final FiguraVec3 tempNormal = FiguraVec3.get();

        public FiguraVertex free() {
            pool.add(this);
            return this;
        }

        public FiguraVertex transformed(FiguraMat4 positionMatrix, FiguraMat3 normalMatrix) {
            tempPos.x = x;
            tempPos.y = y;
            tempPos.z = z;
            tempPos.w = 1;
            tempPos.multiply(positionMatrix);

            tempNormal.x = nx;
            tempNormal.y = ny;
            tempNormal.z = nz;
            tempNormal.multiply(normalMatrix);

            return get((float) tempPos.x, (float) tempPos.y, (float) tempPos.z, u, v, (float) tempNormal.x, (float) tempNormal.y, (float) tempNormal.z);
        }

        private float x, y, z, u, v, nx, ny, nz, r, g, b, a;

    }

}
