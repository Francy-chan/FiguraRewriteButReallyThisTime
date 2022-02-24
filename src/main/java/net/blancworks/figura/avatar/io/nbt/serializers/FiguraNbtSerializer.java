package net.blancworks.figura.avatar.io.nbt.serializers;

import net.minecraft.nbt.NbtElement;

public interface FiguraNbtSerializer<T, S extends NbtElement> {
    S serialize(T data);
}
