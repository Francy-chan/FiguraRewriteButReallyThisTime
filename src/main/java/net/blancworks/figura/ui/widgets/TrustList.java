package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class TrustList extends Panel implements Element {

    private final List<SliderWidget> sliders = new ArrayList<>();

    public TrustList(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);

        //temp
        sliders.clear();
        for (int i = 0; i < 7; i++) {
            SliderWidget slider = new SliderWidget(x + width / 2 + 4, y + 4 + (15 * i), width / 2 - 8, 11, 0.25f, i);
            sliders.add(slider);
            addDrawableChild(slider);
        }

        this.addDrawableChild(new TexturedButton(x + 4, y + 4, 40, 20, new LiteralText("slices"), null, button -> sliders.get(0).setSteps((int) (Math.random() * 10))));
        this.addDrawableChild(new TexturedButton(x + 4, y + 24, 40, 20, new LiteralText("value"), null, button -> {
            float rand = (float) Math.random() * 3 - 1;
            sliders.get(0).setScrollProgress(rand, false);
            System.out.println(rand);
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        //temp
        for (SliderWidget slider : sliders) {
            Text text = Text.of(slider.getScrollProgress() + "");
            drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, text, slider.x - MinecraftClient.getInstance().textRenderer.getWidth(text.asOrderedText()) - 5, slider.y, 0xFF72B7);
        }

        new SliderWidget(x + width / 2 + 4, y + 4 , width / 2 - 8, 11, 1f).render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();

        //render children
        super.render(matrices, mouseX, mouseY, delta);
    }
}
