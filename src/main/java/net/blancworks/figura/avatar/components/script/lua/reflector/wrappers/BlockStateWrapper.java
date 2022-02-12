package net.blancworks.figura.avatar.components.script.lua.reflector.wrappers;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//Wait for vectors ig
public class BlockStateWrapper extends ObjectWrapper<BlockState> {

    private static World getWorld() {
        return MinecraftClient.getInstance().world;
    }

    public boolean isTranslucent(BlockPos pos) {
        return target.isTranslucent(getWorld(), pos);
    }

    public int getOpacity(BlockPos pos) {
        return target.getOpacity(getWorld(), pos);
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

    public int getMapColor(BlockPos pos) {
        return target.getMapColor(getWorld(), pos).color;
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

    public boolean hasEmissiveLighting(BlockPos pos) {
        return target.hasEmissiveLighting(getWorld(), pos);
    }

    public boolean isSolidBlock(BlockPos pos) {
        return target.isSolidBlock(getWorld(), pos);
    }

    public boolean emitsRedstonePower() {
        return target.emitsRedstonePower();
    }

    public boolean isFullCube(BlockPos pos) {
        return target.isFullCube(getWorld(), pos);
    }

    public boolean isToolRequired() {
        return target.isToolRequired();
    }

}
