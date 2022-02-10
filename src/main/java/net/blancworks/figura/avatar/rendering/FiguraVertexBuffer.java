package net.blancworks.figura.avatar.rendering;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FiguraVertexBuffer {

    // -- Variables -- //
    private static final Queue<FiguraVertex> vertexCache = new LinkedList<>(); // Cache of ALL vertices that aren't being used currently by Figura
    public final List<FiguraVertex> vertexList = new ArrayList<>(); // List of vertices in this vertex buffer

    public String renderingMode = "";
    public int textureGroup = -1;


    // -- Functions -- //

    //Submits all the vertices within this buffer to the given VertexConsumer
    public void submitToVertexConsumer(VertexConsumer consumer) {
        for (FiguraVertex vert : vertexList) {
            consumer.vertex(
                    vert.pos.getX(), vert.pos.getY(), vert.pos.getZ(),
                    vert.red, vert.green, vert.blue, vert.alpha,
                    vert.u, vert.v,
                    vert.overlay, vert.light,
                    vert.normal.getX(), vert.normal.getY(), vert.normal.getZ()
            );
        }
    }

    public void clear() {
        vertexCache.addAll(vertexList);
        vertexList.clear();
    }

    public void addVertex(Vector4f pos, Vec3f normal, float red, float blue, float green, float alpha, float u, float v, int overlay, int light) {
        FiguraVertex vertex = vertexCache.poll();
        if (vertex == null) vertex = new FiguraVertex();

        vertex.pos.set(pos.getX(), pos.getY(), pos.getZ());
        vertex.normal.set(normal);
        vertex.red = red;
        vertex.blue = blue;
        vertex.green = green;
        vertex.alpha = alpha;
        vertex.u = u;
        vertex.v = v;
        vertex.overlay = overlay;
        vertex.light = light;

        vertexList.add(vertex);
    }

    // -- Subclasses -- //
    public static class FiguraVertex {
        public final Vec3f pos = new Vec3f();
        public final Vec3f normal = new Vec3f();
        public float u, v;
        public float red, blue, green, alpha;
        public int light, overlay;
    }
}
