package net.blancworks.figura.avatar.newavatar;

import net.blancworks.figura.avatar.newavatar.data.BufferSetBuilder;
import net.blancworks.figura.avatar.rendering.TransformData;
import net.minecraft.client.util.math.MatrixStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class NewFiguraModelPart {

    /**
     * Prepend children with duplicate names using this number.
     * This way multiple children with the "same" name can exist
     * in the map.
     */
    private int dupeStopper = 2;

    private final String name;
    public final TransformData transform;
    private Map<String, NewFiguraModelPart> children;
    private final int[] verticesByBuffer;

    public NewFiguraModelPart(String name, BufferSetBuilder bufferSet) {
        this.name = name;
        verticesByBuffer = new int[bufferSet.numBuffers()];
        transform = new TransformData();
    }

    public void addVertices(int texIndex, int numVerts) {
        verticesByBuffer[texIndex] += numVerts;
    }

    public void addChild(NewFiguraModelPart child) {
        if (children == null)
            children = new LinkedHashMap<>();
        String name = child.name;
        if (children.containsKey(name)) {
            while (children.containsKey(name+dupeStopper))
                dupeStopper++;
            name += dupeStopper;
        }
        children.put(name, child);
    }

    public NewFiguraModelPart getChild(String name) {
        return children == null ? null : children.get(name);
    }

    public void renderImmediate(FiguraBufferSet bufferSet) {
        transform.recalculateMatrix();
        bufferSet.pushTransform(transform);

        for (int i = 0; i < verticesByBuffer.length; i++)
            bufferSet.pushVertices(i, verticesByBuffer[i]);

        if (children != null)
            for (Map.Entry<String, NewFiguraModelPart> entry : children.entrySet()) //using entrySet() for LinkedHashMap iteration order
                entry.getValue().renderImmediate(bufferSet);

        bufferSet.popTransform();
    }

}
