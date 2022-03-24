package net.blancworks.figura.ui.widgets;

import net.blancworks.figura.config.Config;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ConfigWidget implements Drawable, Element, FiguraDrawable {

    private final Config config;
    private final Text title;
    private final Text tooltip;

    private boolean visible = true;

    ConfigWidget(Text title, Text tooltip, Config config) {
        this.config = config;
        this.title = title;
        this.tooltip = tooltip;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    //category
    public static class CategoryConfig extends ConfigWidget {

        private final SwitchButton expandButton;
        private final List<ConfigWidget> children = new ArrayList<>();

        CategoryConfig(Text title, Text tooltip, Config config) {
            super(title, tooltip, config);
            expandButton = new SwitchButton(0, 0, 80, 20, title, tooltip, button -> {}) {
                @Override
                protected void renderText(MatrixStack matrixStack) {
                    super.renderText(matrixStack);

                    //render arrow

                }
            };
        }
    }

    //bool
    public static class BooleanConfig extends ConfigWidget {

        BooleanConfig(Text title, Text tooltip, Config config) {
            super(title, tooltip, config);
        }
    }
}
