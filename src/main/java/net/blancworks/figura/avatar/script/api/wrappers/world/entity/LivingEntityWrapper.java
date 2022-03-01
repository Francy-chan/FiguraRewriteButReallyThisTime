package net.blancworks.figura.avatar.script.api.wrappers.world.entity;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.entity.LivingEntity;

public class LivingEntityWrapper<T extends LivingEntity> extends ObjectWrapper<T> {

    @LuaWhitelist
    public FiguraVec3 getPos() {
        return FiguraVec3.get(target.getPos());
    }
}
