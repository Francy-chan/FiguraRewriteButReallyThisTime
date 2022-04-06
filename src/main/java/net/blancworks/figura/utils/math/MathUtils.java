package net.blancworks.figura.utils.math;

import net.blancworks.figura.utils.math.vector.*;

public class MathUtils {

    public static Object sizedVector(int size, double... vals) {
        return switch (size) {
            case 2 -> FiguraVec2.get(vals[0], vals[1]);
            case 3 -> FiguraVec3.get(vals[0], vals[1], vals[2]);
            case 4 -> FiguraVec4.get(vals[0], vals[1], vals[2], vals[3]);
            case 5 -> FiguraVec5.get(vals[0], vals[1], vals[2], vals[3], vals[4]);
            case 6 -> FiguraVec6.get(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
            default -> throw new IllegalStateException("Cannot create vector of size: " + size);
        };
    }
}
