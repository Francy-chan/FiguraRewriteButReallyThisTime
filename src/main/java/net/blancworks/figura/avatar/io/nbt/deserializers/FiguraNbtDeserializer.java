package net.blancworks.figura.avatar.io.nbt.deserializers;

import net.minecraft.nbt.NbtElement;

public interface FiguraNbtDeserializer<T, S extends NbtElement> {
    T deserialize(S data);
}
