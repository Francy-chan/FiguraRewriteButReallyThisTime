package net.blancworks.figura.utils;

import net.minecraft.util.math.Vec3f;

public class VecMath {

    public static Vec3f lerp(Vec3f a, Vec3f b, float time){
        Vec3f ret = a.copy();
        Vec3f tmp = b.copy();

        tmp.subtract(a);
        tmp.scale(time);
        ret.add(tmp);

        return ret;
    }
}
