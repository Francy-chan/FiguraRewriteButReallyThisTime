package net.blancworks.figura.avatar.script.api.wrappers.world.entity;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityWrapper extends LivingEntityWrapper<PlayerEntity> {

    @LuaWhitelist
    public float getFood() {
        return target.getHungerManager().getFoodLevel();
    }

    @LuaWhitelist
    public float getSaturation() {
        return target.getHungerManager().getSaturationLevel();
    }

    @LuaWhitelist
    public float getExperienceProgress() {
        return target.experienceProgress;
    }

    @LuaWhitelist
    public int getExperienceLevel() {
        return target.experienceLevel;
    }

    @LuaWhitelist
    public String getGamemode() {
        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        PlayerListEntry playerListEntry = null;

        if (networkHandler != null)
            playerListEntry = networkHandler.getPlayerListEntry(target.getGameProfile().getId());

        return playerListEntry == null || playerListEntry.getGameMode() == null ? null : playerListEntry.getGameMode().name();
    }

    @LuaWhitelist
    public boolean isFlying() {
        return target.getAbilities().flying;
    }

}
