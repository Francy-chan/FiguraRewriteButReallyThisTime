package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.blancworks.figura.utils.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TrustList extends Panel implements Element {

    private final List<TrustSlider> sliders = new ArrayList<>();
    private final ScrollBarWidget scrollbar;

    public TrustList(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);

        scrollbar = new ScrollBarWidget(x + width - 14, y + 4, 10, height - 8, 0f);
        addDrawableChild(scrollbar);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        //scrollbar
        int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
        int totalHeight = sliders.size() * (27 + fontHeight); //11 (slider) + font height + 16 (padding)
        scrollbar.y = y + 4;
        scrollbar.visible = totalHeight > height;

        //render sliders
        int sliderX = scrollbar.visible ? 8 : 15;
        int sliderY = scrollbar.visible ? (int) -(MathHelper.lerp(scrollbar.getScrollProgress(), -16, totalHeight - height)) : 16;
        for (TrustSlider slider : sliders) {
            slider.x = x + sliderX;
            slider.y = y + sliderY;
            sliderY += 27 + fontHeight;
        }

        //render children
        super.render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isVisible() && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return isVisible() && (super.mouseScrolled(mouseX, mouseY, amount) || this.scrollbar.mouseScrolled(mouseX, mouseY, amount));
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
        private boolean changed;

        public TrustSlider(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust) {
            super(x, y, width, height, MathHelper.clamp(container.get(trust) / (float) trust.max, 0f, 1f));
            this.trust = trust;
            this.value = trust.checkInfinity(container.get(trust)) ? INFINITY : new LiteralText(String.valueOf(container.get(trust)));
            this.changed = container.getSettings().containsKey(trust);

            setAction(slider -> {
                //update trust
                int value = (int) (trust.max * slider.getScrollProgress());
                boolean infinity = trust.checkInfinity(value);

                container.getSettings().put(trust, infinity ? Integer.MAX_VALUE - 100 : value);
                changed = true;

                //update text
                this.value = infinity ? INFINITY : new LiteralText(String.valueOf(value));
            });
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            //trust name
            MutableText name = new TranslatableText("figura.trust." + trust.name().toLowerCase());
            if (changed) name = new LiteralText("*").setStyle(ColorUtils.Colors.FRAN_PINK.style).append(name).append("*");

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, name, x, y, 0xFFFFFF);

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
