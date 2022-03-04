package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TrustList extends Panel implements Element {

    private final List<TrustSlider> sliders = new ArrayList<>();

    public TrustList(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        //render sliders
        int sliderY = y + 8;
        for (TrustSlider slider : sliders) {
            slider.y = sliderY;
            sliderY += 30 + MinecraftClient.getInstance().textRenderer.fontHeight;
        }

        //render children
        super.render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();
    }

    public void updateList(TrustContainer container) {
        //clear old sliders
        sliders.forEach(this::remove);
        sliders.clear();

        //add new sliders
        for (TrustContainer.Trust trust : TrustContainer.Trust.values()) {
            if (!trust.isToggle) {
                TrustSlider slider = new TrustSlider(x + 8, y, width - 30, 11 + MinecraftClient.getInstance().textRenderer.fontHeight, container, trust);
                sliders.add(slider);
                this.addDrawableChild(slider);
            }
        }
    }

    private static class TrustSlider extends SliderWidget {

        private static final Text INFINITY = new TranslatableText("figura.trust.infinity");

        private final TrustContainer.Trust trust;
        private Text value;

        public TrustSlider(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust) {
            super(x, y, width, height, MathHelper.clamp(container.get(trust) / (float) trust.max, 0f, 1f));
            this.trust = trust;
            this.value = trust.checkInfinity(container.get(trust)) ? INFINITY : new LiteralText(String.valueOf(container.get(trust)));

            setAction(slider -> {
                //update trust
                int value = (int) (trust.max * slider.getScrollProgress());
                boolean infinity = trust.checkInfinity(value);

                container.getSettings().put(trust, infinity ? Integer.MAX_VALUE - 100 : value);

                //update text
                this.value = infinity ? INFINITY : new LiteralText(String.valueOf(value));
            });
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            //trust name
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, new TranslatableText("figura.trust." + trust.name().toLowerCase()), x, y, 0xFFFFFF);

            //trust value
            textRenderer.draw(matrices, value, x + width - textRenderer.getWidth(value), y, Formatting.AQUA.getColorValue());

            //button
            matrices.push();
            matrices.translate(0f, textRenderer.fontHeight, 0f);
            super.renderButton(matrices, mouseX, mouseY, delta);
            matrices.pop();
        }
    }
}
