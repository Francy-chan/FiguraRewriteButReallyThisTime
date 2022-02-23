package net.blancworks.figura.avatar.newavatar.data.deserializers;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.newavatar.NewFiguraModelPart;
import net.blancworks.figura.avatar.newavatar.data.BufferSetBuilder;
import net.blancworks.figura.math.vector.FiguraVec2;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

public class FiguraModelPartDeserializer implements FiguraNbtDeserializer<NewFiguraModelPart, NbtCompound> {

    private final BufferSetBuilder bufferSetBuilder;

    public FiguraModelPartDeserializer(BufferSetBuilder bufferSetBuilder) {
        this.bufferSetBuilder = bufferSetBuilder;
    }

    @Override
    public NewFiguraModelPart deserialize(NbtCompound data) {
        String name = data.getString("name");
        NewFiguraModelPart result = new NewFiguraModelPart(name, bufferSetBuilder);

        readVec3(data, result.transform.origin, "origin");
        readVec3(data, result.transform.rotation, "rotation");

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


    private void readCuboid(NewFiguraModelPart modelPart, NbtCompound data) {
        //Read from and to
        readVec3(data, from, "from");
        readVec3(data, to, "to");

        //Inflate
        float inflate = readValue(data, "inflate");
        from.add(-inflate, -inflate, -inflate);
        to.add(inflate, inflate, inflate);

        ftDiff.copyFrom(to);
        ftDiff.subtract(from);

        if (data.contains("faces"))
            for (String direction : faceData.keySet())
                readFace(data.getCompound("faces"), modelPart, direction);

    }

    private static final FiguraVec3 tempPos = FiguraVec3.get();

    private void readFace(NbtCompound faces, NewFiguraModelPart modelPart, String direction) {
        if (faces.contains(direction)) {
            NbtCompound face = faces.getCompound(direction);
            int texId = (int) readValue(face, "texture");
            modelPart.addVertices(texId, 4);

            FiguraVec3 normal = faceData.get(direction)[4];
            int rotation = (int) readValue(face, "rotation");
            float u1 = readValue(face, "u1");
            float v1 = readValue(face, "v1");
            float u2 = readValue(face, "u2");
            float v2 = readValue(face, "v2");
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

    /**
     * Reads a numeric value with the given name from data.
     * If the value is not in data, returns 0.
     */
    private static float readValue(NbtCompound data, String name) {
        if (data.contains(name, NbtElement.BYTE_TYPE))
            return data.getByte(name);
        if (data.contains(name, NbtElement.FLOAT_TYPE))
            return data.getFloat(name);
        if (data.contains(name, NbtElement.SHORT_TYPE))
            return data.getShort(name);
        return 0;
    }

    private static void readVec3(NbtCompound data, FiguraVec3 target, String name) {
        target.x = readValue(data, name+"X");
        target.y = readValue(data, name+"Y");
        target.z = readValue(data, name+"Z");
    }


}
