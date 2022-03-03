package net.blancworks.figura.avatar.io.nbt.deserializers;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.utils.math.vector.FiguraVec2;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.blancworks.figura.utils.IOUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

public class FiguraModelPartDeserializer implements FiguraNbtDeserializer<FiguraModelPart, NbtCompound> {

    private final BufferSetBuilder bufferSetBuilder;

    public FiguraModelPartDeserializer(BufferSetBuilder bufferSetBuilder) {
        this.bufferSetBuilder = bufferSetBuilder;
    }

    @Override
    public FiguraModelPart deserialize(NbtCompound data) {
        String name = data.getString("name");
        String parentName = data.getString("parent");
        String renderMode = data.getString("render_mode");
        FiguraModelPart result = new FiguraModelPart(name, bufferSetBuilder, parentName, renderMode);

        IOUtils.readVec3(data, result.getTransform().origin, "origin");
        IOUtils.readVec3(data, result.getTransform().rotation, "rotation");

        switch (data.getByte("type")) {
            case 1 -> readCuboid(result, data);
        }

        if (data.contains("children"))
            for (NbtElement child: data.getList("children", NbtElement.COMPOUND_TYPE))
                result.addChild(deserialize((NbtCompound) child));

        return result;
    }


    /**
     * Format is the 4 vertices, then the normal.
     */
    private static final Map<String, FiguraVec3[]> faceData = new ImmutableMap.Builder<String, FiguraVec3[]>()
            .put("north", new FiguraVec3[] {
                    FiguraVec3.get(1, 0, 0),
                    FiguraVec3.get(0, 0, 0),
                    FiguraVec3.get(0, 1, 0),
                    FiguraVec3.get(1, 1, 0),
                    FiguraVec3.get(0, 0, -1)
            })
            .put("south", new FiguraVec3[] {
                    FiguraVec3.get(0, 0, 1),
                    FiguraVec3.get(1, 0, 1),
                    FiguraVec3.get(1, 1, 1),
                    FiguraVec3.get(0, 1, 1),
                    FiguraVec3.get(0, 0, 1)
            })
            .put("east", new FiguraVec3[] {
                    FiguraVec3.get(1, 0, 1),
                    FiguraVec3.get(1, 0, 0),
                    FiguraVec3.get(1, 1, 0),
                    FiguraVec3.get(1, 1, 1),
                    FiguraVec3.get(1, 0, 0)
            })
            .put("west", new FiguraVec3[] {
                    FiguraVec3.get(0, 0, 0),
                    FiguraVec3.get(0, 0, 1),
                    FiguraVec3.get(0, 1, 1),
                    FiguraVec3.get(0, 1, 0),
                    FiguraVec3.get(-1, 0, 0)
            })
            .put("up", new FiguraVec3[] {
                    FiguraVec3.get(0, 1, 1),
                    FiguraVec3.get(1, 1, 1),
                    FiguraVec3.get(1, 1, 0),
                    FiguraVec3.get(0, 1, 0),
                    FiguraVec3.get(0, 1, 0)
            })
            .put("down", new FiguraVec3[] {
                    FiguraVec3.get(0, 0, 0),
                    FiguraVec3.get(1, 0, 0),
                    FiguraVec3.get(1, 0, 1),
                    FiguraVec3.get(0, 0, 1),
                    FiguraVec3.get(0, -1, 0)
            }).build();

    private static FiguraVec2[] uvValues = new FiguraVec2[] {
            FiguraVec2.get(0, 1),
            FiguraVec2.get(1, 1),
            FiguraVec2.get(1, 0),
            FiguraVec2.get(0, 0)
    };

    private static final FiguraVec3 from = FiguraVec3.get();
    private static final FiguraVec3 to = FiguraVec3.get();
    private static final FiguraVec3 ftDiff = FiguraVec3.get();


    private void readCuboid(FiguraModelPart modelPart, NbtCompound data) {
        //Read from and to
        IOUtils.readVec3(data, from, "from");
        IOUtils.readVec3(data, to, "to");

        //Inflate
        double inflate = IOUtils.readValue(data, "inflate");
        from.add(-inflate, -inflate, -inflate);
        to.add(inflate, inflate, inflate);

        ftDiff.copyFrom(to);
        ftDiff.subtract(from);

        if (data.contains("faces"))
            for (String direction : faceData.keySet())
                readFace(data.getCompound("faces"), modelPart, direction);

    }

    private static final FiguraVec3 tempPos = FiguraVec3.get();

    private void readFace(NbtCompound faces, FiguraModelPart modelPart, String direction) {
        if (faces.contains(direction)) {
            NbtCompound face = faces.getCompound(direction);
            int texId = (int) IOUtils.readValue(face, "texture");
            modelPart.addVertices(texId, 4);

            FiguraVec3 normal = faceData.get(direction)[4];
            int rotation = (int) IOUtils.readValue(face, "rotation");
            double u1 = IOUtils.readValue(face, "u1");
            double v1 = IOUtils.readValue(face, "v1");
            double u2 = IOUtils.readValue(face, "u2");
            double v2 = IOUtils.readValue(face, "v2");
            for (int i = 0; i < 4; i++) {
                tempPos.copyFrom(ftDiff);
                tempPos.multiply(faceData.get(direction)[i]);
                tempPos.add(from);

                FiguraVec2 normalizedUv = uvValues[(i + rotation)%4];

                bufferSetBuilder.addVertex(
                        texId,
                        tempPos,
                        (float) MathHelper.lerp(normalizedUv.x, u1, u2),
                        (float) MathHelper.lerp(normalizedUv.y, v1, v2),
                        normal
                );
            }
        }
    }

}
