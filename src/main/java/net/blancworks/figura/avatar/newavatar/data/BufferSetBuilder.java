package net.blancworks.figura.avatar.newavatar.data;

import net.blancworks.figura.avatar.components.texture.FiguraTexture;
import net.blancworks.figura.avatar.newavatar.FiguraBuffer;
import net.blancworks.figura.avatar.newavatar.FiguraBufferSet;
import net.blancworks.figura.math.vector.FiguraVec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BufferSetBuilder {

    private final List<ProtoBuffer> protoBuffers;

    public BufferSetBuilder() {
        protoBuffers = new ArrayList<>();
    }

    public void addTextureSet(byte[] main, byte[] emissive) {
        protoBuffers.add(new ProtoBuffer(main, emissive));
    }

    public int numBuffers() {
        return protoBuffers.size();
    }

    public void addVertex(int bufferIndex, FiguraVec3 pos, float u, float v, FiguraVec3 normal) {
        ProtoBuffer proto = protoBuffers.get(bufferIndex);
        FiguraBuffer.FiguraVertex vertex = FiguraBuffer.FiguraVertex.get(
                (float) pos.x,
                (float) pos.y,
                (float) pos.z,
                u / proto.mainTex.getWidth(),
                v / proto.mainTex.getHeight(), //TODO: deal with emissives here, remember emissives can have different sizes?!?!
                (float) normal.x,
                (float) normal.y,
                (float) normal.z
        );
        protoBuffers.get(bufferIndex).vertexList.add(vertex);
    }

    public FiguraBufferSet build() {
        List<FiguraBuffer> buffers = new LinkedList<>();
        for (ProtoBuffer protoBuffer : protoBuffers)
            buffers.add(protoBuffer.build());
        return new FiguraBufferSet(buffers);
    }

    /**
     * A prototype for a buffer, not yet completed. Can be built into a full FiguraBuffer eventually.
     */
    private static class ProtoBuffer {

        private final FiguraTexture mainTex;
        private final FiguraTexture emissiveTex;
        private final List<FiguraBuffer.FiguraVertex> vertexList;

        public ProtoBuffer(byte[] mainData, byte[] emissiveData) {
            this.mainTex = mainData != null ? new FiguraTexture(mainData) : null;;
            this.emissiveTex = emissiveData != null ? new FiguraTexture(emissiveData) : null;
            vertexList = new LinkedList<>();
        }

        private FiguraBuffer build() {
            return new FiguraBuffer(mainTex, emissiveTex, vertexList);
        }

    }

}
