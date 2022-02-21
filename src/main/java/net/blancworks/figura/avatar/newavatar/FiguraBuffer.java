package net.blancworks.figura.avatar.newavatar;

import net.blancworks.figura.avatar.components.texture.FiguraTexture;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.client.render.RenderLayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FiguraBuffer {

    private FiguraVertex[] vertices;
    private int curIndex;
    private RenderLayer mainLayer;
    private RenderLayer emissiveLayer;

    public FiguraBuffer(RenderLayer mainLayer, RenderLayer emissiveLayer, List<FiguraVertex> vertexList) {
        vertices = vertexList.toArray(new FiguraVertex[0]);
        this.mainLayer = mainLayer;
        this.emissiveLayer = emissiveLayer;
    }



    public static class FiguraVertex {

        private static final Queue<FiguraVertex> pool = new LinkedList<>();
        private FiguraVertex() {}
        public static FiguraVertex get(float x, float y, float z, float u, float v, byte normal) {
            FiguraVertex result = pool.poll();
            if (result == null)
                result = new FiguraVertex();
            result.x = x;
            result.y = y;
            result.z = z;
            result.u = u;
            result.v = v;
            result.normal = normal;
            return result;
        }

        private static final float[] normals = {
                1, 0, 0,
                -1, 0, 0,
                0, 1, 0,
                0, -1, 0,
                0, 0, 1,
                0, 0, -1
        };

        private float x, y, z, u, v;
        private byte normal;

    }

}
