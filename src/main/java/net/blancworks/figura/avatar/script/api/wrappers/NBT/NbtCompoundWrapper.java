package net.blancworks.figura.avatar.script.api.wrappers.NBT;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.script.lua.types.LuaTable;
import net.minecraft.nbt.*;

import java.util.Map;

public class NbtCompoundWrapper extends ObjectWrapper<NbtCompound> {

    private final ImmutableMap<Class<?>, NbtToValue<?>> nbtToValueMap = new ImmutableMap.Builder<Class<?>, NbtToValue<?>>()
            .put(NbtCompound.class, a -> a)
            .put(NbtFloat.class, a -> ((NbtFloat) a).floatValue())
            .put(NbtDouble.class, a -> ((NbtDouble) a).doubleValue())
            .put(NbtInt.class, a -> ((NbtInt) a).intValue())
            .put(NbtLong.class, a -> ((NbtLong) a).longValue())
            .put(NbtByte.class, a -> ((NbtByte) a).byteValue())
            .put(NbtShort.class, a -> ((NbtShort) a).shortValue())
            .put(NbtString.class, NbtElement::asString)
            .build();
    private final ImmutableMap<Class<?>, ValueToNBT> valueToNBTMap = new ImmutableMap.Builder<Class<?>, ValueToNBT>()
            .put(NbtCompound.class, a -> (NbtCompound) a)
            .put(Float.class, a -> smallestElement((float) a))
            .put(Double.class, a -> smallestElement((double) a))
            .put(Integer.class, a -> smallestElement((long) a))
            .put(Short.class, a -> smallestElement((long) a))
            .put(Long.class, a -> smallestElement((long) a))
            .put(Byte.class, a -> smallestElement((long) a))
            .put(String.class, a -> NbtString.of((String) a))
            .put(LuaTable.class, a -> luaTableToNbtCompound((LuaTable) a))
            .build();

    private NbtToValue getNTVConverter(Class<?> tClass) {
        if (tClass == Object.class) return null;

        var converter = nbtToValueMap.get(tClass);

        if (converter == null)
            return getNTVConverter(tClass.getSuperclass());
        return converter;
    }

    private Object elementToObject(NbtElement element) {
        var converter = getNTVConverter(element.getClass());

        if (converter == null) return null;
        return converter.convert(element);
    }

    private ValueToNBT getVTNConverter(Class<?> tClass) {
        if (tClass == Object.class) return null;

        var converter = valueToNBTMap.get(tClass);

        if (converter == null)
            return getVTNConverter(tClass.getSuperclass());
        return converter;
    }

    private NbtElement objectToElement(Object obj) {
        var converter = getVTNConverter(obj.getClass());

        if (converter == null) return null;
        return converter.convert(obj);
    }

    private NbtElement smallestElement(long value) {
        if (value >= -128 && value <= 127)
            return NbtByte.of((byte) value);
        if (value >= -32768 && value <= 32767)
            return NbtShort.of((short) value);
        if (value >= -2147483648 && value <= 2147483647)
            return NbtInt.of((int) value);
        return NbtLong.of(value);
    }

    private NbtElement smallestElement(float value) {
        if (value - Math.floor(value) < 0.0001f)
            return smallestElement((long) Math.floor(value));
        return NbtFloat.of(value);
    }

    private NbtElement smallestElement(double value) {
        if (value - Math.floor(value) < 0.0001f)
            return smallestElement((long) Math.floor(value));
        return NbtDouble.of(value);
    }

    private NbtCompound luaTableToNbtCompound(LuaTable tbl) {
        NbtCompound newCompound = new NbtCompound();

        for (Map.Entry<Object, Object> entry : tbl.entrySet()) {
            String newKey = entry.getKey().toString();
            NbtElement newVal = objectToElement(entry.getValue());

            newCompound.put(newKey, newVal);
        }

        return newCompound;
    }

    @Override
    public Object getFallback(String key) {
        return elementToObject(target.get(key));
    }

    @Override
    public void setFallback(String key, Object value) {
        var elementFromVal = objectToElement(value);

        if (elementFromVal != null)
            target.put(key, elementFromVal);
    }

    @FunctionalInterface
    private interface NbtToValue<T> {
        T convert(NbtElement element);
    }

    @FunctionalInterface
    private interface ValueToNBT {
        NbtElement convert(Object object);
    }
}
