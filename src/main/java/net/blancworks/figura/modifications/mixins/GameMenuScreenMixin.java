package net.blancworks.figura.modifications.mixins;

import net.blancworks.figura.ui.gui.FiguraWardrobeScreen;
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
    private FiguraWardrobeScreen figuraWardrobe;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("RETURN"), method = "initWidgets")
    void initWidgets(CallbackInfo ci) {
        if (this.figuraWardrobe == null)
            this.figuraWardrobe = new FiguraWardrobeScreen(this);

        int x = 5;
        int y = 5;

        int config = 4;
        switch (config) {
            case 1: //top right
                x = this.width - 64 - 5;
                break;
            case 2: //bottom left
                y = this.height - 20 - 5;
                break;
            case 3: //bottom right
                x = this.width - 64 - 5;
                y = this.height - 20 - 5;
                break;
            case 4: //icon
                x = this.width / 2 + 4 + 100 + 2;
                y = this.height / 4 + 96 + -16;
                break;
        }

        Identifier iconTexture = new Identifier("figura", "textures/gui/config_icon.png");
        addDrawableChild(new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, iconTexture, 20, 40, btn -> this.client.setScreen(figuraWardrobe)));
    }
}
