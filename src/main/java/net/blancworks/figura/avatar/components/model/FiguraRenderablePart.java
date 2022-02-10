package net.blancworks.figura.avatar.components.model;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.blancworks.figura.avatar.rendering.FiguraRenderingState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for classes that render visuals on the model
 */
public class FiguraRenderablePart extends FiguraModelPart {

    // -- Vertex Data --

    private final HashMap<Integer, Layer> layers = new HashMap<>();


    /**
     * Adds a vertex to the part.
     */
    public void addVertex(Integer textureGroupID, Vec3f pos, Vec3f normal, float u, float v, int color) {
        layers.computeIfAbsent(textureGroupID, Layer::new).addVertex(
                pos, normal, u, v, color
        );
    }

    /**
     * Clears all vertex data out of the part.
     */
    public void clear() {
        layers.clear();
    }


    // -- Rendering --

    //Cached and re-usable vertices!
    //We actually have these here to reduce allocations and improve cache locality.
    public static final Vector4f posVertex = new Vector4f();
    public static final Vec3f normalVertex = new Vec3f();

    @Override
    public <T extends Entity> void render(FiguraRenderingState<T> renderingState) {
        super.render(renderingState);
        renderingState.poseStack.push();

        //Get matrices from stack
        Matrix4f modelMatrix = renderingState.poseStack.peek().getPositionMatrix();
        Matrix3f normalMatrix = renderingState.poseStack.peek().getNormalMatrix();

        transformation.applyToStack(renderingState.poseStack);

        for (Map.Entry<Integer, Layer> entry : layers.entrySet()) {
            entry.getValue().render(renderingState, modelMatrix, normalMatrix);
        }

        renderingState.poseStack.pop();
    }


    /**
     * Each layer determines a single texture to use for the model.
     */
    public static class Layer {
        /**
         * Stores the vertex data for the part
         */
        private final FloatArrayList vertexData = new FloatArrayList();
        private final IntArrayList colorData = new IntArrayList();

        private int vertexCount = 0;

        public int textureGroup;
        public String renderingMode = "CUTOUT_NO_CULL";

        public Layer(int group){
            this.textureGroup = group;
        }

        public void addVertex(Vec3f pos, Vec3f normal, float u, float v, int color) {
            vertexData.add(pos.getX());
            vertexData.add(pos.getY());
            vertexData.add(pos.getZ());

            vertexData.add(u);
            vertexData.add(v);

            vertexData.add(normal.getX());
            vertexData.add(normal.getY());
            vertexData.add(normal.getZ());

            colorData.add(color);

            vertexCount++;
        }

        public <T extends Entity> void render(FiguraRenderingState<T> renderingState, Matrix4f modelMatrix, Matrix3f normalMatrix) {
            try {
                renderingState.setTextureGroup(textureGroup);
                renderingState.setRenderMode(renderingMode);

                //For each vertex
                for (int i = 0; i < vertexCount; i++) {
                    int startIndex = i * 8;

                    posVertex.set(
                            vertexData.getFloat(startIndex++) / 16.0f,
                            vertexData.getFloat(startIndex++) / 16.0f,
                            vertexData.getFloat(startIndex++) / 16.0f,
                            1
                    );

                    float u = vertexData.getFloat(startIndex++);
                    float v = vertexData.getFloat(startIndex++);

                    normalVertex.set(
                            vertexData.getFloat(startIndex++),
                            vertexData.getFloat(startIndex++),
                            vertexData.getFloat(startIndex++)
                    );

                    posVertex.transform(modelMatrix);
                    normalVertex.transform(normalMatrix);

                    renderingState.vertex(
                            posVertex, normalVertex, 1, 1, 1, 1, u, v
                    );
                }
            } catch (Exception e) {

            }
        }
    }
}
