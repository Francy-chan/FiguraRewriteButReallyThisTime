package net.blancworks.figura.avatar.io.converters;

/**
 * Like Nbt serializers, but a bit less specific.
 * These don't work with NBT necessarily, they are just used to convert one type of object into another.
 * If you want to work with NBT, make a serializer and deserializer.
 * @param <T>
 * @param <S>
 */
public interface FiguraConverter<T, S> {
    S convert(T data);
}
