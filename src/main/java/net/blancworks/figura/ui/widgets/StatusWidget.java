package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

public class StatusWidget implements Drawable, Element, Selectable {

    public static final char[] STATUS_INDICATORS = {'-', '*', '/', '+'};
    public static final List<Style> TEXT_COLORS = List.of(
            Style.EMPTY.withColor(Formatting.WHITE),
            Style.EMPTY.withColor(Formatting.RED),
            Style.EMPTY.withColor(Formatting.YELLOW),
            Style.EMPTY.withColor(Formatting.GREEN)
    );

    private final TextRenderer textRenderer;
    private byte status = 0;
    private Text disconnectedReason;

    public int x, y;

    public StatusWidget(int x, int y) {
        this.x = x;
        this.y = y;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
    }

    public void tick() {
        //update status indicators
        int model = 2;
        status = (byte) model;

        int texture = 0;
        status += (byte) (texture << 2);

        int script = 3;
        status += (byte) (script << 4);

        int backend = 1;
        status += (byte) (backend << 6);
        if (backend != 3) disconnectedReason = null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //status text
        Text statusText = getStatus(0).append("  ").append(getStatus(1)).append("  ").append(getStatus(2)).append("  ").append(getStatus(3));
        UIHelper.renderSliced(matrices, x, y, textRenderer.getWidth(statusText) + 4, textRenderer.fontHeight + 5, UIHelper.OUTLINE);
        UIHelper.drawTextWithShadow(matrices, textRenderer, statusText, x + 2, y + 3, 0xFFFFFF);

        //get status text tooltip
        MutableText text = null;
        if (mouseY >= y && mouseY <= textRenderer.fontHeight + y + 5) {
            String part = "figura.gui.status.";

            //model
            if (mouseX >= x + 2 && mouseX <= x + 12) {
                int color = status & 3;
                text = new TranslatableText(part += "model").append("\n").append(new TranslatableText(part + "." + color)).setStyle(TEXT_COLORS.get(color));
            }
            //texture
            else if (mouseX >= x + 21 && mouseX <= x + 31) {
                int color = status >> 2 & 3;
                text = new TranslatableText(part += "texture").append("\n").append(new TranslatableText(part + "." + color)).setStyle(TEXT_COLORS.get(color));
            }
            //script
            else if (mouseX >= x + 40 && mouseX <= x + 50) {
                int color = status >> 4 & 3;
                text = new TranslatableText(part += "script").append("\n").append(new TranslatableText(part + "." + color)).setStyle(TEXT_COLORS.get(color));
            }
            //backend
            else if (mouseX >= x + 59 && mouseX <= x + 69) {
                int color = status >> 6 & 3;
                text = new TranslatableText(part += "backend").append("\n").append(new TranslatableText(part + "." + color)).setStyle(TEXT_COLORS.get(color));

                if (disconnectedReason != null)
                    text.append("\n \n").append(new TranslatableText(part + ".reason")).append(":\n  ").append(disconnectedReason);
            }
        }

        //render tooltip
        if (text != null) {
            UIHelper.renderTooltip(matrices, text, mouseX, mouseY);
        }
    }

    private MutableText getStatus(int type) {
        return new LiteralText(String.valueOf(STATUS_INDICATORS[status >> (type * 2) & 3])).setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
    }

    @Override
    public SelectionType getType() {
        return SelectionType.HOVERED;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }
}
