package net.blancworks.figura.modifications.mixins.client.gui.hud;

import net.blancworks.figura.avatar.customizations.NameplateCustomizations;
import net.blancworks.figura.modifications.accessors.FiguraMetadataHolder;
import net.blancworks.figura.serving.entity.FiguraMetadata;
import net.blancworks.figura.utils.TextUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.MessageType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "addChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ClientChatListener;onChatMessage(Lnet/minecraft/network/MessageType;Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void addChatMessage(ClientChatListener instance, MessageType type, Text message, UUID sender) {
        if (this.client.player == null || this.client.world == null)
            return;

        for (UUID uuid : this.client.player.networkHandler.getPlayerUuids()) {
            //get player
            PlayerListEntry player = this.client.player.networkHandler.getPlayerListEntry(uuid);
            if (player == null)
                continue;

            UUID id = player.getProfile().getId();
            FiguraMetadataHolder holder = (FiguraMetadataHolder) this.client.world.getPlayerByUuid(id);
            if (holder == null)
                continue;

            FiguraMetadata metadata = holder.getFiguraMetadata();
            NameplateCustomizations.NameplateCustomization custom = metadata.entityFinalCustomizations.nameplateCustomizations.chatNameplate;

            if (custom.text != null) {
                Text replacement = NameplateCustomizations.applyNameplateCustomizations(custom.text);
                message = TextUtils.replaceInText(message, "\\b" + player.getProfile().getName() + "\\b", replacement);
            }
        }

        instance.onChatMessage(type, message, sender);
    }
}
