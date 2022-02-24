package net.blancworks.figura.avatar.model;

import net.blancworks.figura.avatar.io.nbt.deserializers.BufferSetBuilder;
import net.blancworks.figura.utils.TransformData;

import java.util.LinkedHashMap;
import java.util.Map;

public class FiguraModelPart {

    /**
     * Prepend children with duplicate names using this number.
     * This way multiple children with the "same" name can exist
     * in the map.
     */
    private int dupeStopper = 2;

    private final String name;
    public final TransformData transform;
    private Map<String, FiguraModelPart> children;
    private final int[] verticesByBuffer;

    public FiguraModelPart(String name, BufferSetBuilder bufferSet) {
        this.name = name;
        verticesByBuffer = new int[bufferSet.numBuffers()];
        transform = new TransformData();
    }

    public void addVertices(int texIndex, int numVerts) {
        verticesByBuffer[texIndex] += numVerts;
    }

    public void addChild(FiguraModelPart child) {
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

    public FiguraModelPart getChild(String name) {
        return children == null ? null : children.get(name);
    }

    public void renderImmediate(FiguraBufferSet bufferSet) {
        transform.recalculateMatrix();
        bufferSet.pushTransform(transform);

        for (int i = 0; i < verticesByBuffer.length; i++)
            bufferSet.pushVertices(i, verticesByBuffer[i]);

        if (children != null)
            for (Map.Entry<String, FiguraModelPart> entry : children.entrySet()) //using entrySet() for LinkedHashMap iteration order
                entry.getValue().renderImmediate(bufferSet);

        bufferSet.popTransform();
    }

}
