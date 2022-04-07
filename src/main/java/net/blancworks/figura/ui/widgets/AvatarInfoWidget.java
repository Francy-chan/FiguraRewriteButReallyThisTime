package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.utils.ColorUtils;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;

public class AvatarInfoWidget implements Drawable, Element, FiguraDrawable, FiguraTickable {

    public static final List<Text> TITLES = List.of(
            new TranslatableText("figura.gui.name").formatted(Formatting.UNDERLINE),
            new TranslatableText("figura.gui.size").formatted(Formatting.UNDERLINE),
            new TranslatableText("figura.gui.complexity").formatted(Formatting.UNDERLINE)
    );

    public int x, y;
    public int width, height;
    private boolean visible = true;

    private final TextRenderer textRenderer;
    private final List<Text> values = Arrays.asList(new Text[3]);

    public AvatarInfoWidget(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.textRenderer = MinecraftClient.getInstance().textRenderer;

        this.width = width;
        this.height = (textRenderer.fontHeight + 4) * 6 + 4; //font + spacing + border
    }

    @Override
    public void tick() {
        if (!visible) return;

        //update values
        values.set(0, new LiteralText("AAAAAAAAAAAAAA").setStyle(ColorUtils.Colors.FRAN_PINK.style)); //name
        values.set(1, new LiteralText("BBBBBBBBBBB").setStyle(ColorUtils.Colors.FRAN_PINK.style)); //size
        values.set(2, new LiteralText("CCCCCCCCCCCCCC").setStyle(ColorUtils.Colors.FRAN_PINK.style)); //complexity
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!visible) return;

        //render background
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);

        //prepare vars
        int x = this.x + width / 2;
        int y = this.y + 4;
        int height = textRenderer.fontHeight + 4;

        //render texts
        for (int i = 0; i < 3; i++) {
            //title
            Text title = TITLES.get(i);
            if (title != null)
                UIHelper.drawCenteredTextWithShadow(matrices, textRenderer, title.asOrderedText(), x, y, 0xFFFFFF);
            y += height;

            //value
            Text value = values.get(i);
            if (value != null) {
                Text toRender = TextUtils.trimToWidthEllipsis(textRenderer, value, width - 10);
                UIHelper.drawCenteredTextWithShadow(matrices, textRenderer, toRender.asOrderedText(), x, y, 0xFFFFFF);

                //tooltip
                if (value != toRender && UIHelper.isMouseOver(this.x, y - height, width, height * 2 - 4, mouseX, mouseY))
                    UIHelper.setTooltip(value);
            }
            y += height;
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
