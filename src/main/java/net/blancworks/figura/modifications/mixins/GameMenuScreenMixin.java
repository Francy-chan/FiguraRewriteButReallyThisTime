package net.blancworks.figura.modifications.mixins;

import net.blancworks.figura.ui.FiguraGuiScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {

    @Unique
    private FiguraGuiScreen figuraGuiScreen;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    void initWidgets(CallbackInfo ci) {
        if (this.figuraGuiScreen == null)
            this.figuraGuiScreen = new FiguraGuiScreen(this);

        int x = 5;
        int y = 5;

        int config = 4;
        switch (config) {
            case 1 -> //top right
                    x = this.width - 69;
            case 2 -> //bottom left
                    y = this.height - 25;
            case 3 -> { //bottom right
                x = this.width - 69;
                y = this.height - 25;
            }
            case 4 -> { //icon
                x = this.width / 2 + 106;
                y = this.height / 4 + 80;
            }
        }

        Identifier iconTexture = new Identifier("figura", "textures/gui/config_icon.png");
        addDrawableChild(new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, iconTexture, 20, 40, btn -> this.client.setScreen(figuraGuiScreen)));
    }
}
