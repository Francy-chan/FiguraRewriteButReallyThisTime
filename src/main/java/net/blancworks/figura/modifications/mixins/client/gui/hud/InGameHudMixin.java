package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.config.Config;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.trust.TrustContainer;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.UUID;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @ModifyArgs(method = "addChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void onChatMessage(Args args) {
        if (this.client.player == null || !(boolean) Config.CHAT_MODIFICATIONS.value)
            return;

        for (UUID uuid : this.client.player.networkHandler.getPlayerUuids()) {
            //get player
            PlayerListEntry player = this.client.player.networkHandler.getPlayerListEntry(uuid);
            if (player == null)
                continue;

            //get metadata
            UUID id = player.getProfile().getId();
            FiguraMetadata metadata = FiguraHouse.getMetadata(id);

            //apply customization
            Text message = args.get(1);
            Text replacement;

            NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.chatNameplate;
            if (custom.text != null && metadata.trustContainer.get(TrustContainer.Trust.NAMEPLATE_EDIT) == 1) {
                replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text.replaceAll("\n|\\\\n", ""));
            } else {
                replacement = new LiteralText(player.getProfile().getName());
            }

            //apply badges
            if ((boolean) Config.BADGES.value) {
                Text badges = NameplateCustomizations.fetchBadges(metadata);
                if (badges != null) ((MutableText) replacement).append(badges);
            }

            //modify text
            args.set(1, TextUtils.replaceInText(message, "\\b" + player.getProfile().getName() + "\\b", replacement));
        }
    }
}
