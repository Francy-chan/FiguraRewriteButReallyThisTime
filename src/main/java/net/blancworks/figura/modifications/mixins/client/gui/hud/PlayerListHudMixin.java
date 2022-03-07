package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.entity.FiguraEntityMetadata;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(at = @At("RETURN"), method = "getPlayerName", cancellable = true)
    private void getPlayerName(PlayerListEntry player, CallbackInfoReturnable<Text> cir) {
        if (this.client.player == null || this.client.world == null)
            return;

        //get metadata
        UUID id = player.getProfile().getId();
        FiguraMetadataHolder holder = (FiguraMetadataHolder) this.client.world.getPlayerByUuid(id);
        if (holder == null)
            return;

        //get customization
        FiguraEntityMetadata<?> metadata = holder.getFiguraMetadata();
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
