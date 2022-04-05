package net.blancworks.figura.ui.widgets.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blancworks.figura.config.Config;
import net.blancworks.figura.ui.helpers.UIHelper;
import net.blancworks.figura.ui.widgets.config.ConfigWidget;
import net.blancworks.figura.ui.widgets.config.InputElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ConfigList extends AbstractList {

    private static final List<ConfigWidget> CONFIGS = new ArrayList<>();
    public KeyBinding focusedBinding;

    public ConfigList(int x, int y, int width, int height) {
        super(x, y, width, height);
        updateList();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //background and scissors
        UIHelper.renderSliced(matrices, x, y, width, height, UIHelper.OUTLINE);
        UIHelper.setupScissor(x + scissorsX, y + scissorsY, width + scissorsWidth, height + scissorsHeight);

        //scrollbar
        int totalHeight = -4;
        for (ConfigWidget config : CONFIGS)
            totalHeight += config.getHeight() + 8;
        int entryHeight = totalHeight / CONFIGS.size();

        scrollBar.visible = totalHeight > height;
        scrollBar.setScrollRatio(entryHeight, totalHeight - height);

        //render list
        int xOffset = scrollBar.visible ? 4 : 11;
        int yOffset = scrollBar.visible ? (int) -(MathHelper.lerp(scrollBar.getScrollProgress(), -4, totalHeight - height)) : 4;
        for (ConfigWidget config : CONFIGS) {
            config.setPos(x + xOffset, y + yOffset);
            yOffset += config.getHeight() + 8;
        }

        //children
        super.render(matrices, mouseX, mouseY, delta);

        //reset scissor
        RenderSystem.disableScissor();

        //render overlays
        super.renderOverlays(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //fix mojang focusing for text fields
        for (ConfigWidget configWidget : CONFIGS) {
            for (Element element : configWidget.children()) {
                if (element instanceof InputElement inputElement)
                    inputElement.getTextField().getField().setTextFieldFocused(inputElement.getTextField().isMouseOver(mouseX, mouseY));
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void updateList() {
        //clear old widgets
        CONFIGS.forEach(children::remove);

        //temp list
        List<ConfigWidget> temp = new ArrayList<>();

        //add configs
        ConfigWidget lastCategory = null;
        for (Config config : Config.values()) {
            //add new config entry into the category
            if (config.type != Config.ConfigType.CATEGORY) {
                //create dummy category if empty
                if (lastCategory == null) {
                    ConfigWidget widget = new ConfigWidget(width - 22, LiteralText.EMPTY, null, this);
                    lastCategory = widget;

                    temp.add(widget);
                    children.add(widget);
                }

                //add entry
                lastCategory.addConfig(config);
            //add new config category
            } else {
                ConfigWidget widget = new ConfigWidget(width - 22, config.name, config.tooltip, this);
                lastCategory = widget;

                temp.add(widget);
                children.add(widget);
            }
        }

        //fix expanded status
        if (!CONFIGS.isEmpty()) {
            for (int i = 0; i < CONFIGS.size(); i++)
                temp.get(i).setShowChildren(CONFIGS.get(i).isShowingChildren());
        }

        //add configs
        CONFIGS.clear();
        CONFIGS.addAll(temp);
    }
}
