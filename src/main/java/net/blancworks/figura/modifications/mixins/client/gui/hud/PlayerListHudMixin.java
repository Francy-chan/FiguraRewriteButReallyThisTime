package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(at = @At("RETURN"), method = "getPlayerName", cancellable = true)
    private void getPlayerName(PlayerListEntry player, CallbackInfoReturnable<Text> cir) {
        //get customization
        FiguraMetadata metadata = FiguraHouse.getMetadata(player.getProfile().getId());
        NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.tablistNameplate;

        //apply customization
        if (custom.text != null) {
            Text text = cir.getReturnValue();

            Text replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
            text = TextUtils.replaceInText(text, "\\b" + player.getProfile().getName() + "\\b", replacement);

            cir.setReturnValue(text);
        }
    }
}
