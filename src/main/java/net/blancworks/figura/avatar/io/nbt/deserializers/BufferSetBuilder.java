package net.blancworks.figura.avatar.io.nbt.deserializers;

import net.blancworks.figura.avatar.model.FiguraBuffer;
import net.blancworks.figura.avatar.model.FiguraBufferSet;
import net.blancworks.figura.avatar.texture.FiguraTexture;
import net.blancworks.figura.utils.math.vector.FiguraVec3;

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
                u / proto.getWidth(),
                v / proto.getHeight(),
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

        public int getWidth() {
            if (mainTex != null)
                return mainTex.getWidth();
            return emissiveTex.getWidth();
        }

        public int getHeight() {
            if (mainTex != null)
                return mainTex.getHeight();
            return emissiveTex.getHeight();
        }

        private FiguraBuffer build() {
            return new FiguraBuffer(mainTex, emissiveTex, vertexList);
        }

    }

}
