package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

public class StatusWidget implements Drawable, Element, FiguraDrawable, FiguraTickable {

    public static final String STATUS_INDICATORS = "-*/+";
    public static final List<String> STATUS_NAMES = List.of("model", "texture", "script", "backend");
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
    public int width, height;
    private boolean visible = true;

    public StatusWidget(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;
        this.width = width;
        this.height = textRenderer.fontHeight + 5;
    }

    @Override
    public void tick() {
        if (!visible) return;

        //update status indicators
        int model = 2;
        status = (byte) model;

        int texture = 0;
        status += (byte) (texture << 2);

        int script = 3;
        status += (byte) (script << 4);

        int backend = 1;
        status += (byte) (backend << 6);
        if (backend != 3) disconnectedReason = new LiteralText("your mom!");
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //background
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);

        //text
        float size = (width - 44) / 3f + 10;
        for (int i = 0; i < 4; i++) {
            Text text = getStatus(i);
            UIHelper.drawTextWithShadow(matrices, textRenderer, text, (int) (x + size * i + 2), y + 3, 0xFFFFFF);
        }

        //mouse over
        this.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!UIHelper.isMouseOver(x, y, width, height, mouseX, mouseY))
            return false;

        //get status text tooltip
        MutableText text = null;
        String part = "figura.gui.status.";

        float size = (width - 44) / 3f + 10;
        for (int i = 0; i < 4; i++) {
            double x = this.x + 7 + size * i - size / 2f; //x + 2 spacing + 5 half icon + size - half size
            if (mouseX >= x && mouseX <= x + size) {
                //get name and color
                int color = status >> (i * 2) & 3;
                text = new TranslatableText(part += STATUS_NAMES.get(i)).append("\n• ").append(new TranslatableText(part + "." + color)).setStyle(TEXT_COLORS.get(color));

                //get backend disconnect reason
                if (i == 3 && disconnectedReason != null)
                    text.append("\n").append("\n").append(new TranslatableText(part + ".reason")).append("\n• ").append(disconnectedReason);

                break;
            }
        }

        //set tooltip
        UIHelper.setTooltip(text);

        return true;
    }

    private MutableText getStatus(int type) {
        return new LiteralText(String.valueOf(STATUS_INDICATORS.charAt(status >> (type * 2) & 3))).setStyle(Style.EMPTY.withFont(TextUtils.FIGURA_FONT));
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
