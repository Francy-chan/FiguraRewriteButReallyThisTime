package net.blancworks.figura.avatar.script.api.wrappers.world;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class WorldWrapper extends ObjectWrapper<World> {

    @LuaWhitelist
    public int getLightLevel(int x, int y, int z) {
        return target.getLightLevel(new BlockPos(x, y, z));
    }

    @LuaWhitelist
    public int getLightLevel(FiguraVec3 pos) {
        return target.getLightLevel(pos.getBlockPos());
    }

    @LuaWhitelist
    public int getBlockLightLevel(int x, int y, int z) {
        return target.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
    }

    @LuaWhitelist
    public int getBlockLightLevel(FiguraVec3 pos) {
        return target.getLightLevel(LightType.BLOCK, pos.getBlockPos());
    }


    @LuaWhitelist
    public BlockState getBlockState(int x, int y, int z) {
        return target.getBlockState(new BlockPos(x, y, z));
    }

    @LuaWhitelist
    public BlockState getBlockState(FiguraVec3 pos) {
        return target.getBlockState(pos.getBlockPos());
    }

    @LuaWhitelist
    public boolean isRaining() {
        return target.isRaining();
    }

    @LuaWhitelist
    public boolean isDay() {
        return target.isDay();
    }

    @LuaWhitelist
    public long getTime() {
        return target.getTime();
    }
}
