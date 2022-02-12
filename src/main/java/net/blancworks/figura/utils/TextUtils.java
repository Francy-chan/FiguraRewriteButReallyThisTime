package net.blancworks.figura.utils;

import com.mojang.brigadier.StringReader;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {

    public static String noBadges4U(String string) {
        return string.replaceAll("([▲★✯☆✭]|\\\\u(?i)(25B2|2605|272F|2606|272D))", "\uFFFD");
    }

    public static List<Text> splitText(Text text, String regex) {
        ArrayList<Text> textList = new ArrayList<>();

        MutableText currentText = new LiteralText("");
        for (Text entry : text.getWithStyle(text.getStyle())) {
            String entryString = entry.getString();
            String[] lines = entryString.split(regex);
            for (int i = 0; i < lines.length; i++) {
                if (i != 0) {
                    textList.add(currentText.shallowCopy());
                    currentText = new LiteralText("");
                }
                currentText.append(new LiteralText(lines[i]).setStyle(entry.getStyle()));
            }
            if (entryString.endsWith(regex)) {
                textList.add(currentText.shallowCopy());
                currentText = new LiteralText("");
            }
        }
        textList.add(currentText);

        return textList;
    }

    public static void removeClickableObjects(MutableText text) {
        text.setStyle(text.getStyle().withClickEvent(null));

        for (Text child : text.getSiblings()) {
            removeClickableObjects((MutableText) child);
        }
    }

    public static void renderOutlineText(TextRenderer textRenderer, Text text, float x, float y, int color, int outline, MatrixStack matrices) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                textRenderer.draw(matrices, text, x + i, y + j, outline);
            }
        }

        matrices.push();
        matrices.translate(0f, 0f, 0.1f);
        textRenderer.draw(matrices, text, x, y, color);
        matrices.pop();
    }

    public static Text tryParseJson(String text) {
        Text ret;

        try {
            ret = Text.Serializer.fromJson(new StringReader(text));

            if (ret == null)
                throw new Exception("Error parsing JSON string");
        } catch (Exception ignored) {
            ret = new LiteralText(text);
        }

        return ret;
    }
}
