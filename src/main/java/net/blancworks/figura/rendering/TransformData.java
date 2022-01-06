package net.blancworks.figura.rendering;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class TransformData {
    public Vec3f position;
    public Quaternion rotation;
    public Vec3f scale;

    public void readFromNBT(NbtCompound tag) {
        tag.putFloat("tx", position.getX());
        tag.putFloat("ty", position.getX());
        tag.putFloat("tz", position.getX());

        tag.putFloat("rx", rotation.getX());
        tag.putFloat("ry", rotation.getY());
        tag.putFloat("rz", rotation.getZ());
        tag.putFloat("rw", rotation.getW());

        tag.putFloat("sx", scale.getX());
        tag.putFloat("sy", scale.getY());
        tag.putFloat("sz", scale.getZ());
    }

    public void writeToNBT(NbtCompound tag) {
        position = new Vec3f(
                tag.getFloat("tx"),
                tag.getFloat("ty"),
                tag.getFloat("tz")
        );

        rotation = new Quaternion(
                tag.getFloat("rx"),
                tag.getFloat("ry"),
                tag.getFloat("rz"),
                tag.getFloat("rx")
        );

        scale = new Vec3f(
                tag.getFloat("sx"),
                tag.getFloat("sy"),
                tag.getFloat("sz")
        );
    }
}
