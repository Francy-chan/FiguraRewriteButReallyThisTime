package net.blancworks.figura.avatar.rendering;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.Vec3f;

public class TransformData {
    public final Vec3f position = new Vec3f();
    public final Vec3f origin = new Vec3f();
    public final Vec3f rotation = new Vec3f();
    public final Vec3f scale = new Vec3f(1, 1, 1);

    public void readFromNBT(NbtCompound tag) {
        NbtList originList = tag.getList("origin", NbtElement.FLOAT_TYPE);
        NbtList rotList = tag.getList("rotation", NbtElement.FLOAT_TYPE);

        origin.set(originList.getFloat(0), originList.getFloat(1), originList.getFloat(2));
        rotation.set(rotList.getFloat(0), rotList.getFloat(1), rotList.getFloat(2));
    }

    public void applyToStack(MatrixStack matrices) {
        matrices.translate(origin.getX() / 16.0f, origin.getY() / 16.0f, origin.getZ() / 16.0f);

        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(rotation.getZ()));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation.getY()));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(rotation.getX()));

        matrices.translate(position.getX() / 16.0f, position.getY() / 16.0f, position.getZ() / 16.0f);

        matrices.translate(-origin.getX() / 16.0f, -origin.getY() / 16.0f, -origin.getZ() / 16.0f);
    }
}
