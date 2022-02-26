package net.blancworks.figura.avatar.model;

import net.blancworks.figura.avatar.io.nbt.deserializers.BufferSetBuilder;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.blancworks.figura.utils.RenderingUtils;
import net.blancworks.figura.utils.TransformData;

import java.util.LinkedHashMap;
import java.util.Map;

public class FiguraModelPart {

    /**
     * Append children with duplicate names using this number.
     * This way multiple children with the "same" name can exist
     * in the map.
     */
    private int dupeStopper = 2;

    private final String name;
    public final TransformData transform;
    private Map<String, FiguraModelPart> children;
    private final int[] verticesByBuffer;

    private String renderMode;
    private final String parentName;

    public FiguraModelPart(String name, BufferSetBuilder bufferSet, String parentName, String renderMode) {
        this.name = name;
        verticesByBuffer = new int[bufferSet.numBuffers()];
        transform = new TransformData();
        this.parentName = parentName;
        this.renderMode = renderMode;
    }

    public void addVertices(int texIndex, int numVerts) {
        verticesByBuffer[texIndex] += numVerts;
    }

    public void addChild(FiguraModelPart child) {
        if (children == null)
            children = new LinkedHashMap<>();
        String name = child.name;
        if (children.containsKey(name)) {
            while (children.containsKey(name + dupeStopper))
                dupeStopper++;
            name += dupeStopper;
        }
        children.put(name, child);
    }

    public FiguraModelPart getChild(String name) {
        return children == null ? null : children.get(name);
    }

    private static final FiguraVec3 vanillaPivotTemp = FiguraVec3.get();

    public void renderImmediate(FiguraBufferSet bufferSet) {
        var parentPart = RenderingUtils.vanillaModelData.getData(parentName);

        if (parentPart != null) {

            //Get difference between vanilla origin and custom origin
            vanillaPivotTemp.set(parentPart.origin);
            vanillaPivotTemp.subtract(transform.origin);

            //Correct position and pivot by difference
            transform.position.set(vanillaPivotTemp);
            transform.bonusOrigin.set(vanillaPivotTemp);

            //Set rotation
            transform.rotation.set(parentPart.rotation);
            transform.needsMatrixRecalculation = true;
        }

        transform.recalculateMatrix();
        bufferSet.pushModifications(transform, renderMode);

        int i = 0;
        while (i < verticesByBuffer.length && bufferSet.pushVertices(i, verticesByBuffer[i++]));

        if (children != null)
            for (Map.Entry<String, FiguraModelPart> entry : children.entrySet()) //using entrySet() for LinkedHashMap iteration order
                entry.getValue().renderImmediate(bufferSet);

        bufferSet.popTransform();
    }

}
