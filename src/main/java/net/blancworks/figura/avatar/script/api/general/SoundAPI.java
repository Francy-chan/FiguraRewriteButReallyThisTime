package net.blancworks.figura.avatar.script.api.general;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.FiguraVec2;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SoundAPI extends ObjectWrapper<SoundAPI> {

    @LuaWhitelist
    public void playSound(String id, FiguraVec3 position, FiguraVec2 volPitch) {
        if (volPitch == null)
            volPitch = FiguraVec2.get(1f, 1f);

        SoundEvent event = Registry.SOUND_EVENT.get(new Identifier(id));
        if (event == null)
            return;

        World w = MinecraftClient.getInstance().world;
        if (MinecraftClient.getInstance().isPaused() || w == null)
            return;

        w.playSound(position.x, position.y, position.z, event, SoundCategory.PLAYERS, (float) volPitch.x, (float) volPitch.y, true);
    }
}
