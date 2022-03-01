package net.blancworks.figura.utils;

import net.minecraft.util.math.Vec3f;

import java.awt.*;

public class ColorUtils {

    public static final Vec3f ACE_BLUE = new Vec3f(0xAF / 255f, 0xF2 / 255f, 1f); //0xAFF2FF
    public static final Vec3f FRAN_PINK = new Vec3f(1f, 0x72 / 255f, 0xB7 / 255f); //0xFF72B7
    public static final Vec3f LILY_RED = new Vec3f(1f, 0x24 / 255f, 0f); //0xFF2400
    public static final Vec3f MAYA_BLUE = new Vec3f(0x0C / 255f, 0xE0 / 255f, 0xCE / 255f); //0x0CE0CE
    public static final Vec3f NICE = new Vec3f(0x69 / 255f, 0x69 / 255f, 0x69 / 255f); //0x696969

    public static int[] split(int value, int len) {
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            int shift = (len * 8) - ((i + 1) * 8);
            array[i] = value >> shift & 0xFF;
        }

        return array;
    }

    public static Vec3f hexToRGB(int hex) {
        int[] rgb = ColorUtils.split(hex, 3);
        return new Vec3f(rgb[0] / 255f, rgb[1] / 255f, rgb[2] / 255f);
    }

    public static Vec3f hexStringToRGB(String hex, Vec3f fallback) {
        //parse #
        if (hex.startsWith("#")) hex = hex.substring(1);

        //return
        try {
            return hexToRGB(Integer.parseInt(hex, 16));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    public static Vec3f hexStringToRGB(String string) {
        //parse hex color
        StringBuilder hex = new StringBuilder(string);

        if (hex.toString().startsWith("#")) hex = new StringBuilder(hex.substring(1));
        if (hex.length() < 6) {
            char[] bgChar = hex.toString().toCharArray();

            //special catch for 3
            if (hex.length() == 3)
                hex = new StringBuilder("" + bgChar[0] + bgChar[0] + bgChar[1] + bgChar[1] + bgChar[2] + bgChar[2]);
            else
                hex.append("0".repeat(Math.max(0, 6 - hex.toString().length())));
        }

        //return
        try {
            return hexToRGB(Integer.parseInt(hex.toString(), 16));
        } catch (Exception ignored) {
            return Vec3f.ZERO.copy();
        }
    }

    public static Vec3f hsvToRGB(Vec3f hsv) {
        int hex = Color.HSBtoRGB(hsv.getX(), hsv.getY(), hsv.getZ());
        return hexToRGB(hex);
    }
}
