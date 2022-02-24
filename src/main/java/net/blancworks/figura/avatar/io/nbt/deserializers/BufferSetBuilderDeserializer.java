package net.blancworks.figura.avatar.io.nbt.deserializers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

/**
 * Creates a BufferSetBuilder and fills its textures in. Does NOT add the vertices.
 */
public class BufferSetBuilderDeserializer implements FiguraNbtDeserializer<BufferSetBuilder, NbtList> {

    private static BufferSetBuilderDeserializer INSTANCE = new BufferSetBuilderDeserializer();

    public static BufferSetBuilderDeserializer getInstance() {
        return INSTANCE;
    }

    @Override
    public BufferSetBuilder deserialize(NbtList data) {
        BufferSetBuilder result = new BufferSetBuilder();

        for (NbtElement element : data) {
            NbtCompound texCompound = (NbtCompound) element;
            byte[] main = texCompound.getByteArray("main");
            byte[] emissive = texCompound.getByteArray("emissive");

            if (main.length == 0) main = null;
            if (emissive.length == 0) emissive = null;

            result.addTextureSet(main, emissive);
        }

        return result;
    }
}
