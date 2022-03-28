package net.blancworks.figura.ui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.lists.AbstractList;
import net.minecraft.client.util.math.MatrixStack;

public class ConfigList extends AbstractList {

    //private final List<ConfigCategory> categories = new ArrayList<>();

    public ConfigList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight);

        //children
        super.render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();
    }
}
