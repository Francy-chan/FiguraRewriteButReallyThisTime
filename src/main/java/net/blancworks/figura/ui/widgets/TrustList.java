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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class TrustList extends Panel implements Element {

    private final List<TrustSlider> sliders = new ArrayList<>();
    private final List<TrustSwitch> switches = new ArrayList<>();
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
        int entryHeight = 27 + fontHeight; //11 (slider) + font height + 16 (padding)
        int totalHeight = (sliders.size() + switches.size()) * entryHeight;
        scrollbar.y = y + 4;
        scrollbar.visible = totalHeight > height;
        scrollbar.setScrollRatio(entryHeight, totalHeight - height);

        //render sliders
        int xOffset = scrollbar.visible ? 8 : 15;
        int yOffset = scrollbar.visible ? (int) -(MathHelper.lerp(scrollbar.getScrollProgress(), -16, totalHeight - height)) : 16;
        for (TrustSlider slider : sliders) {
            slider.x = x + xOffset;
            slider.y = y + yOffset;
            yOffset += 27 + fontHeight;
        }

        //render switches
        for (TrustSwitch trustSwitch : switches) {
            trustSwitch.x = x + xOffset;
            trustSwitch.y = y + yOffset;
            yOffset += 27 + fontHeight;
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
        return isVisible() && (this.scrollbar.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount));
    }

    public void updateList(TrustContainer container) {
        //clear old sliders
        sliders.forEach(this::remove);
        sliders.clear();

        //clear old switches
        switches.forEach(this::remove);
        switches.clear();

        //add new sliders
        for (TrustContainer.Trust trust : TrustContainer.Trust.values()) {
            int fontHeight = MinecraftClient.getInstance().textRenderer.fontHeight;
            if (!trust.isToggle) {
                TrustSlider slider = new TrustSlider(x + 8, y, width - 30, 11 + fontHeight, container, trust);
                sliders.add(slider);
                this.addDrawableChild(slider);
            } else {
                TrustSwitch trustSwitch = new TrustSwitch(x + 8, y, width - 30, 20 + fontHeight, container, trust);
                switches.add(trustSwitch);
                this.addDrawableChild(trustSwitch);
            }
        }
    }

    private static class TrustSlider extends SliderWidget {

        private static final Text INFINITY = new TranslatableText("figura.trust.infinity");

        private final TrustContainer.Trust trust;
        private Text value;
        private boolean changed;

        public TrustSlider(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust) {
            super(x, y, width, height, MathHelper.clamp(container.get(trust) / (trust.max + 1f), 0f, 1f));
            this.trust = trust;
            this.value = trust.checkInfinity(container.get(trust)) ? INFINITY : new LiteralText(String.valueOf(container.get(trust)));
            this.changed = container.getSettings().containsKey(trust);

            setAction(slider -> {
                //update trust
                int value = (int) ((trust.max + 1f) * slider.getScrollProgress());
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

    private static class TrustSwitch extends SwitchButton {

        private final TrustContainer container;
        private final TrustContainer.Trust trust;
        private Text value;
        private boolean changed;

        public TrustSwitch(int x, int y, int width, int height, TrustContainer container, TrustContainer.Trust trust) {
            super(x, y, width, height, trust.asBoolean(container.get(trust)));
            this.container = container;
            this.trust = trust;
            this.changed = container.getSettings().containsKey(trust);
            this.value = new TranslatableText("figura.trust." + (toggled ? "enabled" : "disabled"));
        }

        @Override
        public void onPress() {
            //update trust
            boolean value = !this.isToggled();

            this.container.getSettings().put(trust, value ? 1 : 0);
            this.changed = true;

            //update text
            this.value = new TranslatableText("figura.trust." + (value ? "enabled" : "disabled"));

            super.onPress();
        }

        @Override
        protected void renderTexture(MatrixStack matrices, float delta) {
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
            super.renderTexture(matrices, delta);
            matrices.pop();
        }
    }
}
