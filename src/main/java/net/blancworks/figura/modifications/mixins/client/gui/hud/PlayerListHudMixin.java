package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(at = @At("RETURN"), method = "getPlayerName", cancellable = true)
    private void getPlayerName(PlayerListEntry player, CallbackInfoReturnable<Text> cir) {
        //get customization
        UUID id = player.getProfile().getId();
        FiguraMetadata metadata = FiguraHouse.getMetadata(id);

        //trust check
        if (metadata.trustContainer.get(TrustContainer.Trust.NAMEPLATE_EDIT) == 0)
            return;

        //apply customization
        NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.tablistNameplate;
        if (custom.text != null) {
            Text text = cir.getReturnValue();

            Text replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
            text = TextUtils.replaceInText(text, "\\b" + player.getProfile().getName() + "\\b", replacement);

            cir.setReturnValue(text);
        }
    }
}
