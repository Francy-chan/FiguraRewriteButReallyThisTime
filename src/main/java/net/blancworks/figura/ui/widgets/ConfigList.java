package net.blancworks.figura.ui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.panels.Panel;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ConfigList extends Panel implements Element {

    //private final List<ConfigCategory> categories = new ArrayList<>();
    private final ScrollBarWidget scrollbar;

    public ConfigList(int x, int y, int width, int height) {
        super(x, y, width, height, LiteralText.EMPTY);

        scrollbar = new ScrollBarWidget(x + width - 14, y + 4, 10, height - 8, 0f);
        addDrawableChild(scrollbar);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + 1, y + 1, width - 2, height - 2);

        //children
        super.render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return this.scrollbar.mouseScrolled(mouseX, mouseY, amount) || super.mouseScrolled(mouseX, mouseY, amount);
    }
}
