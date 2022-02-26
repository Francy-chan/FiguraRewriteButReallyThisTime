package net.blancworks.figura.avatar.script.api.wrappers.block;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateWrapper extends ObjectWrapper<BlockState> {

    @LuaWhitelist
    private static World getWorld() {
        return MinecraftClient.getInstance().world;
    }

    @LuaWhitelist
    public boolean isTranslucent(FiguraVec3 pos) {
        return target.isTranslucent(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    @LuaWhitelist
    public int getOpacity(FiguraVec3 pos) {
        return target.getOpacity(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    @LuaWhitelist
    public boolean exceedsCube() {
        return target.exceedsCube();
    }

    @LuaWhitelist
    public boolean hasSidedTransparency() {
        return target.hasSidedTransparency();
    }

    @LuaWhitelist
    public int getLuminance() {
        return target.getLuminance();
    }

    @LuaWhitelist
    public boolean isAir() {
        return target.isAir();
    }

    @LuaWhitelist
    public int getMapColor(FiguraVec3 pos) {
        return target.getMapColor(getWorld(), new BlockPos(pos.x, pos.y, pos.z)).color;
    }

    @LuaWhitelist
    public BlockState rotateClockwise(int degrees) {
        int r = ((degrees % 360) + 360) % 360; //0 to 359
        return switch (r) {
            case 0 -> target;
            case 90 -> target.rotate(BlockRotation.CLOCKWISE_90);
            case 180 -> target.rotate(BlockRotation.CLOCKWISE_180);
            case 270 -> target.rotate(BlockRotation.COUNTERCLOCKWISE_90);
            default -> null;
        };
    }

    @LuaWhitelist
    public boolean hasEmissiveLighting(FiguraVec3 pos) {
        return target.hasEmissiveLighting(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    @LuaWhitelist
    public boolean isSolidBlock(FiguraVec3 pos) {
        return target.isSolidBlock(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    @LuaWhitelist
    public boolean emitsRedstonePower() {
        return target.emitsRedstonePower();
    }

    @LuaWhitelist
    public boolean isFullCube(FiguraVec3 pos) {
        return target.isFullCube(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    @LuaWhitelist
    public boolean isToolRequired() {
        return target.isToolRequired();
    }

}
