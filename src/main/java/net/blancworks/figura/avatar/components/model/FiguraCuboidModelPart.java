package net.blancworks.figura.avatar.components.model;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

public class FiguraCuboidModelPart extends FiguraRenderablePart {

    public FiguraCuboidModelPart() {
        type = 1;
    }

    @Override
    public void readFromNBT(NbtCompound tag) {
        super.readFromNBT(tag);

        float inflate = tag.getFloat("inflate");


        Vec3f from = vec3FromNbt("from", tag);
        Vec3f to = vec3FromNbt("to", tag);

        Vec3f mid = from.copy();
        mid.lerp(to, 0.5f);

        //Inflation (owo)
        from.subtract(mid);
        from.add(-inflate, -inflate, -inflate);
        from.add(mid);
        to.subtract(mid);
        to.add(inflate, inflate, inflate);
        to.add(mid);

        transformation.position.copyFrom(from);

        to.subtract(from);
        from.set(0,0,0);

        NbtList faces = tag.getList("faces", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < 6; i++)
            buildFace(faces, i, from, to);
    }

    // -- Building --


    /**
     * Builds a face for the cuboid from the given compound + face index combo
     */
    public void buildFace(NbtList list, int faceIndex, Vec3f from, Vec3f to) {
        //Do nothing if face is not found for whatever reason.
        NbtCompound faceData = list.getCompound(faceIndex);

        //Integer ID of the texture group for this face
        int textureGroupID = faceData.getInt("texture");

        //No texture, don't build this face.
        if (textureGroupID == -1)
            return;

        int rotation = faceData.getInt("rotation") / 90;

        NbtList uvList = faceData.getList("uv", NbtElement.FLOAT_TYPE);
        Vector4f uv = new Vector4f(uvList.getFloat(0), uvList.getFloat(1), uvList.getFloat(2), uvList.getFloat(3));

        buildVertices(faceIndex, from, to, textureGroupID, uv, rotation);
    }

    //Vertex positons for faces, accessed by faceid,vertex
    private static final Vec3f[][] vertexValues = new Vec3f[][]{
            //North
            {
                    new Vec3f(1, 0, 0),
                    new Vec3f(0, 0, 0),
                    new Vec3f(0, 1, 0),
                    new Vec3f(1, 1, 0),
            },
            //South
            {
                    new Vec3f(0, 0, 1),
                    new Vec3f(1, 0, 1),
                    new Vec3f(1, 1, 1),
                    new Vec3f(0, 1, 1),

            },
            //East
            {
                    new Vec3f(1, 0, 1),
                    new Vec3f(1, 0, 0),
                    new Vec3f(1, 1, 0),
                    new Vec3f(1, 1, 1),
            },
            //West
            {
                    new Vec3f(0, 0, 0),
                    new Vec3f(0, 0, 1),
                    new Vec3f(0, 1, 1),
                    new Vec3f(0, 1, 0),
            },
            //Top
            {
                    new Vec3f(0, 1, 1),
                    new Vec3f(1, 1, 1),
                    new Vec3f(1, 1, 0),
                    new Vec3f(0, 1, 0),
            },
            //Bottom
            {
                    new Vec3f(0, 0, 0),
                    new Vec3f(1, 0, 0),
                    new Vec3f(1, 0, 1),
                    new Vec3f(0, 0, 1),
            },
    };

    //Uv values for faces, accessed by rotation,vertex
    private static final Vec2f[][] uvValues = new Vec2f[][]{
            {
                    new Vec2f(0, 1),
                    new Vec2f(1, 1),
                    new Vec2f(1, 0),
                    new Vec2f(0, 0),
            },
            {
                    new Vec2f(1, 1),
                    new Vec2f(1, 0),
                    new Vec2f(0, 0),
                    new Vec2f(0, 1),
            },
            {
                    new Vec2f(1, 0),
                    new Vec2f(0, 0),
                    new Vec2f(0, 1),
                    new Vec2f(1, 1),
            },
            {
                    new Vec2f(0, 0),
                    new Vec2f(0, 1),
                    new Vec2f(1, 1),
                    new Vec2f(1, 0),
            },
    };

    private static final Vec3f[] normals = new Vec3f[]{
            new Vec3f(0, 0, -1),
            new Vec3f(0, 0, 1),
            new Vec3f(1, 0, 0),
            new Vec3f(-1, 0, 0),
            new Vec3f(0, 1, 0),
            new Vec3f(0, -1, 0)
    };

    /**
     * Builds the vertices for a face into the face itself.
     * TODO - Add UV
     */
    private void buildVertices(int faceIndex, Vec3f from, Vec3f to, int textureGroupID, Vector4f uv, int rotation) {
        //Calculate difference between to and from.
        Vec3f ftDiff = to.copy();
        ftDiff.subtract(from);

        //Get properties for this face
        Vec3f normal = normals[faceIndex];
        Vec3f[] verts = vertexValues[faceIndex];
        Vec2f[] uvs = uvValues[rotation];

        //Create temp vector3 for speeeeddddd
        Vec3f tmpPos = new Vec3f(0, 0, 0);

        //For each vertex
        for (int i = 0; i < verts.length; i++) {
            //Get the scale of the vertex and UV
            Vec3f scale = verts[i];
            Vec2f uvScale = uvs[i];

            //Use temporary vector to calculate vertex position, using scale.
            tmpPos.set(ftDiff);
            tmpPos.multiplyComponentwise(scale.getX(), scale.getY(), scale.getZ());
            tmpPos.add(from);

            //Add vertex to model.
            addVertex(textureGroupID,
                    tmpPos, normal,
                    MathHelper.lerp(uvScale.x, uv.getX(), uv.getZ()), MathHelper.lerp(uvScale.y, uv.getY(), uv.getW()),
                    0
            );
        }
    }

}
