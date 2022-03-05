package net.blancworks.figura.avatar.script.api.wrappers.world.entity.effect;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.minecraft.entity.effect.StatusEffectInstance;

public class StatusEffectInstanceWrapper extends ObjectWrapper<StatusEffectInstance> {

    @LuaWhitelist
    public float getDuration() {
        return target.getDuration();
    }

    @LuaWhitelist
    public float getAmplifier() {
        return target.getAmplifier();
    }

    @Override
    public Object getFallback(String key) {
        if (key.equals("duration"))
            return target.getDuration();
        if (key.equals("amplifier"))
            return target.getAmplifier();
        return super.getFallback(key);
    }
}
