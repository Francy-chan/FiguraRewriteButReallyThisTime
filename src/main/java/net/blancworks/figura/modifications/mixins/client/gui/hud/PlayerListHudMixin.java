package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.config.Config;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Inject(at = @At("RETURN"), method = "getPlayerName", cancellable = true)
    private void getPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (!(boolean) Config.PLAYERLIST_MODIFICATIONS.value)
            return;

        //get data
        UUID uuid = entry.getProfile().getId();
        FiguraMetadata metadata = FiguraHouse.getMetadata(uuid);

        //apply customization
        Text text = cir.getReturnValue();
        Text replacement;

        NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.tablistNameplate;
        if (custom.text != null && metadata.trustContainer.get(TrustContainer.Trust.NAMEPLATE_EDIT) == 1) {
            replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
        } else {
            replacement = new LiteralText(entry.getProfile().getName());
        }

        //append badges
        if ((boolean) Config.BADGES.value) {
            Text badges = NameplateCustomizations.fetchBadges(metadata);
            if (badges != null) ((MutableText) replacement).append(badges);
        }

        //replace text
        text = TextUtils.replaceInText(text, "\\b" + entry.getProfile().getName() + "\\b", replacement);
        cir.setReturnValue(text);
    }
}
