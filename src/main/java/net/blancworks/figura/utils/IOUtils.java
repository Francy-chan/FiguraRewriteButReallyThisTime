package net.blancworks.figura.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class IOUtils {

    public static void storeVec3(NbtCompound nbt, JsonObject json, String name) {
        if (json.has(name)) {
            JsonArray arr = json.getAsJsonArray(name);
            storeSmallest(nbt, name+"X", arr.get(0).getAsDouble());
            storeSmallest(nbt, name+"Y", arr.get(1).getAsDouble());
            storeSmallest(nbt, name+"Z", arr.get(2).getAsDouble());
        }
    }

    public static void storeSmallest(NbtCompound nbt, String name, double value) {
        double rint = Math.rint(value);
        if (Math.abs(rint - value) < 0.00001)
            value = rint;
        if (value == 0)
            return;
        if (rint == value) {
            if (value <= 127 && value >= -128)
                nbt.putByte(name, (byte) value);
            else if (value <= 32767 && value >= -32768)
                nbt.putShort(name, (short) value);
            else
                nbt.putInt(name, (int) value);
        } else {
            nbt.putFloat(name, (float) value);
        }
    }

    /**
     * Stores the value if value is non-null.
     * @param nbt The nbt compound to store the value in.
     * @param name The name under which to store the value.
     * @param value The value to store. Nothing is stored if this is null.
     */
    public static void storeString(NbtCompound nbt, String name, String value) {
        if (value != null)
            nbt.putString(name, value);
    }

    /**
     * Reads a numeric value with the given name from data.
     * If the value is not in data, returns 0.
     */
    public static double readValue(NbtCompound data, String name) {
        if (!data.contains(name))
            return 0;
        return switch (data.get(name).getType()) {
            case NbtElement.BYTE_TYPE -> data.getByte(name);
            case NbtElement.FLOAT_TYPE -> data.getFloat(name);
            case NbtElement.SHORT_TYPE -> data.getShort(name);
            case NbtElement.INT_TYPE -> data.getInt(name);
            default -> 0;
        };
    }

    public static void readVec3(NbtCompound data, FiguraVec3 target, String name) {
        target.x = readValue(data, name+"X");
        target.y = readValue(data, name+"Y");
        target.z = readValue(data, name+"Z");
    }




}
