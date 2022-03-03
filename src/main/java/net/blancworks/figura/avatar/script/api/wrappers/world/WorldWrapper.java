package net.blancworks.figura.avatar.script.api.wrappers.world;

import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.utils.math.vector.FiguraVec3;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class WorldWrapper extends ObjectWrapper<World> {


    // -- Lighting -- //

    /**
     * Returns the light level from the world at a position.
     *
     * @param x The X Coordinate to get the light level from.
     * @param y The Y Coordinate to get the light level from.
     * @param z The Z Coordinate to get the light level from.
     * @return The light level from the world.
     */
    @LuaWhitelist
    public int getLightLevel(int x, int y, int z) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(new BlockPos(x, y, z));
    }

    /**
     * Returns the light level for the world at a position.
     *
     * @param pos The position to get the light level from.
     * @return The light level from the world.
     */
    @LuaWhitelist
    public int getLightLevel(FiguraVec3 pos) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(pos.getBlockPos());
    }

    /**
     * Returns the block light level from the world at a position.
     *
     * @param x The X Coordinate to get the light level from.
     * @param y The Y Coordinate to get the light level from.
     * @param z The Z Coordinate to get the light level from.
     * @return The block light level from the world.
     */
    @LuaWhitelist
    public int getBlockLightLevel(int x, int y, int z) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(LightType.BLOCK, new BlockPos(x, y, z));
    }

    /**
     * Returns the block light level from the world at a position.
     *
     * @param pos The position to get the light level from.
     * @return The block light level from the world.
     */
    @LuaWhitelist
    public int getBlockLightLevel(FiguraVec3 pos) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(LightType.BLOCK, pos.getBlockPos());
    }

    /**
     * Returns the sky light level from the world at a position.
     *
     * @param x The X Coordinate to get the light level from.
     * @param y The Y Coordinate to get the light level from.
     * @param z The Z Coordinate to get the light level from.
     * @return The sky light level from the world.
     */
    @LuaWhitelist
    public int getSkyLight(int x, int y, int z) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(LightType.SKY, new BlockPos(x, y, z));
    }

    /**
     * Returns the sky light level from the world at a position.
     *
     * @param pos The position to get the light level from.
     * @return The sky light level from the world.
     */
    @LuaWhitelist
    public int getSkyLight(FiguraVec3 pos) {
        target.calculateAmbientDarkness();
        return target.getLightLevel(LightType.SKY, pos.getBlockPos());
    }

    /**
     * Returns if the block is exposed to open sky.
     *
     * @param x The X position of the block to check.
     * @param y The Y position of the block to check.
     * @param z The Z position of the block to check.
     * @return true if exposed to sky, false otherwise.
     */
    @LuaWhitelist
    public boolean isOpenSky(int x, int y, int z) {
        return target.isSkyVisible(new BlockPos(x, y, z));
    }

    /**
     * Returns if the block is exposed to open sky.
     *
     * @param pos The position of the block to check.
     * @return true if exposed to sky, false otherwise.
     */
    @LuaWhitelist
    public boolean isOpenSky(FiguraVec3 pos) {
        return target.isSkyVisible(pos.getBlockPos());
    }

    // -- Block States -- //

    /**
     * Gets the BlockState at a given position.
     *
     * @param x The X position of the block.
     * @param y The Y position of the block.
     * @param z The Z position of the block.
     * @return The block state at the given coordinates, or nil if the block at that position isn't loaded.
     */
    @LuaWhitelist
    public BlockState getBlockState(int x, int y, int z) {
        return target.getBlockState(new BlockPos(x, y, z));
    }

    /**
     * Gets the BlockState at a given position.
     *
     * @param pos The position of the block.
     * @return The block state at the given coordinates, or nil if the block at that position isn't loaded.
     */
    @LuaWhitelist
    public BlockState getBlockState(FiguraVec3 pos) {
        return target.getBlockState(pos.getBlockPos());
    }

    /**
     * Gets the redstone power of a given position.
     *
     * @param x The X position to sample the redstone power from.
     * @param y The Y position to sample the redstone power from.
     * @param z The Z position to sample the redstone power from.
     * @return The redstone power at the position.
     */
    @LuaWhitelist
    public int getRedstonePower(int x, int y, int z) {
        return target.getReceivedRedstonePower(new BlockPos(x, y, z));
    }

    /**
     * Gets the redstone power of a given position.
     *
     * @param pos The position to sample the redstone power from.
     * @return The redstone power at the position.
     */
    @LuaWhitelist
    public int getRedstonePower(FiguraVec3 pos) {
        return target.getReceivedRedstonePower(pos.getBlockPos());
    }


    /**
     * Gets the strong redstone power of a given position.
     *
     * @param x The X position to sample the redstone power from.
     * @param y The Y position to sample the redstone power from.
     * @param z The Z position to sample the redstone power from.
     * @return The strong redstone power at the position.
     */
    @LuaWhitelist
    public int getStrongRedstonePower(int x, int y, int z) {
        return target.getReceivedStrongRedstonePower(new BlockPos(x, y, z));
    }

    /**
     * Gets the strong redstone power of a given position.
     *
     * @param pos The position to sample the redstone power from.
     * @return The strong redstone power at the position.
     */
    @LuaWhitelist
    public int getStrongRedstonePower(FiguraVec3 pos) {
        return target.getReceivedStrongRedstonePower(pos.getBlockPos());
    }


    // -- Nature -- //

    /**
     * Gets a biome at a position.
     * @param x The X position to sample the biome from.
     * @param y The Y position to sample the biome from.
     * @param z The Z position to sample the biome from.
     * @return The biome at the position, nil if the position is not loaded.
     */
    @LuaWhitelist
    public Biome getBiome(int x, int y, int z){
        return target.getBiome(new BlockPos(x,y,z)).value();
    }

    @LuaWhitelist
    /**
     * Gets a biome at a position.
     * @param pos The position to sample the biome from.
     * @return The biome at the position, nil if the position is not loaded.
     */
    public Biome getBiome(FiguraVec3 pos){
        return target.getBiome(pos.getBlockPos()).value();
    }

    /**
     * Gets if it's currently raining.
     *
     * @return True if it's raining, false otherwise.
     */
    @LuaWhitelist
    public boolean isRaining() {
        return target.isRaining();
    }

    /**
     * Returns the gradient of rain, used for smoothing rain graphics between ticks.
     *
     * @param delta The tick delta to sample from.
     * @return 0-1, where 0 is no rain, and 1 is full rain.
     */
    @LuaWhitelist
    public float getRainGradient(float delta) {
        return target.getRainGradient(delta);
    }


    /**
     * Gets if the world is actively lit with lightning.
     *
     * @return True if there is lightning, false otherwise.
     */
    @LuaWhitelist
    public boolean isLightning() {
        return target.isThundering();
    }

    /**
     * Gets if it's currently day.
     *
     * @return True if day, false otherwise.
     */
    @LuaWhitelist
    public boolean isDay() {
        return target.isDay();
    }

    /**
     * Gets the phase of the moon as an integer.
     *
     * @return The phase of the moon.
     */
    @LuaWhitelist
    public int getMoonPhase() {
        return target.getMoonPhase();
    }

    /**
     * Gets the time of day of the world
     *
     * @return A number representing the time of day in ticks.
     */
    @LuaWhitelist
    public long getTimeOfDay() {
        return target.getTimeOfDay();
    }

    /**
     * Gets the current time of the world.
     *
     * @return A number representing the time of the world in ticks.
     */
    @LuaWhitelist
    public long getTime() {
        return target.getTime();
    }
}
