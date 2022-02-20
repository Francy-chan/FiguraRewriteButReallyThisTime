package net.blancworks.figura.utils;

import com.google.gson.JsonArray;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class IOUtils {

    public static FiguraVec3 vec3FromJson(JsonArray arr) {
        FiguraVec3 result = FiguraVec3.get();
        result.x = arr.get(0).getAsDouble();
        result.y = arr.get(1).getAsDouble();
        result.z = arr.get(2).getAsDouble();
        return result;
    }

    public static NbtList vec3ToNbt(FiguraVec3 vec) {
        NbtList result = new NbtList();
        result.add(NbtFloat.of((float) vec.x));
        result.add(NbtFloat.of((float) vec.y));
        result.add(NbtFloat.of((float) vec.z));
        return result;
    }


}
