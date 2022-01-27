package net.blancworks.figura.rendering;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class TransformData {
    public Vec3f origin = new Vec3f();
    public Vec3f rotation = new Vec3f();
    public Vec3f scale = new Vec3f(1, 1, 1);

    public void readFromNBT(NbtCompound tag) {
        NbtList posList = tag.getList("origin", NbtElement.FLOAT_TYPE);
        NbtList rotList = tag.getList("rotation", NbtElement.FLOAT_TYPE);

        origin = new Vec3f(posList.getFloat(0), posList.getFloat(1), posList.getFloat(2));
        rotation = new Vec3f(rotList.getFloat(0), rotList.getFloat(1), rotList.getFloat(2));
    }

    public void applyToStack(MatrixStack matrices) {
        matrices.translate(origin.getX() / 16.0f, origin.getY() / 16.0f, origin.getZ() / 16.0f);

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation.getZ()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation.getY()));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotation.getX()));

        matrices.translate(-origin.getX() / 16.0f, -origin.getY() / 16.0f, -origin.getZ() / 16.0f);
    }
}
