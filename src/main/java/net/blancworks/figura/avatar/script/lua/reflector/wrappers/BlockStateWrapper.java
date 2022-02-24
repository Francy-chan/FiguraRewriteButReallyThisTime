package net.blancworks.figura.avatar.script.lua.reflector.wrappers;

import net.blancworks.figura.math.vector.FiguraVec3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStateWrapper extends ObjectWrapper<BlockState> {

    private static World getWorld() {
        return MinecraftClient.getInstance().world;
    }

    public boolean isTranslucent(FiguraVec3 pos) {
        return target.isTranslucent(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    public int getOpacity(FiguraVec3 pos) {
        return target.getOpacity(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    public boolean exceedsCube() {
        return target.exceedsCube();
    }

    public boolean hasSidedTransparency() {
        return target.hasSidedTransparency();
    }

    public int getLuminance() {
        return target.getLuminance();
    }

    public boolean isAir() {
        return target.isAir();
    }

    public int getMapColor(FiguraVec3 pos) {
        return target.getMapColor(getWorld(), new BlockPos(pos.x, pos.y, pos.z)).color;
    }

    //idk about this one
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

    public boolean hasEmissiveLighting(FiguraVec3 pos) {
        return target.hasEmissiveLighting(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    public boolean isSolidBlock(FiguraVec3 pos) {
        return target.isSolidBlock(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    public boolean emitsRedstonePower() {
        return target.emitsRedstonePower();
    }

    public boolean isFullCube(FiguraVec3 pos) {
        return target.isFullCube(getWorld(), new BlockPos(pos.x, pos.y, pos.z));
    }

    public boolean isToolRequired() {
        return target.isToolRequired();
    }

}
