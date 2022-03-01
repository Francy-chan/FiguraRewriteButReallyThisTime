package net.blancworks.figura.avatar.script.api.wrappers.world;

import net.blancworks.figura.avatar.script.api.math.VectorsAPI;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.math.vector.FiguraVec2;
import net.blancworks.figura.math.vector.FiguraVec3;
import net.blancworks.figura.math.vector.FiguraVec4;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class BiomeWrapper extends ObjectWrapper<Biome> {

    /**
     * Returns the ID of the biome.
     * For example, this may return `minecraft:plains`.
     *
     * @return The ID of the biome, represented as a string.
     */
    @LuaWhitelist
    public String getID() {
        return MinecraftClient.getInstance().world.getRegistryManager().get(Registry.BIOME_KEY).getId(target).toString();
    }

    //Doesn't look like this exists anymore?...
    public String getCategory() {
        return null;
    }

    /**
     * Gets the temperature of the biome. Refer to <a href="https://minecraft.fandom.com/wiki/Biome#Temperature">The Minecraft Wiki</> for more information
     *
     * @return The temperature of the biome.
     */
    @LuaWhitelist
    public float getTemperature() {
        return target.getTemperature();
    }

    /**
     * Gets the precipitation type for the biome.
     *
     * @return 'none', 'rain', or 'snow'
     */
    @LuaWhitelist
    public String getPrecipitation() {
        return target.getPrecipitation().asString();
    }


    /**
     * Returns a Vec4 representing the color of the sky in this biome.
     *
     * @return a Vec4 representing the sky color.
     */
    @LuaWhitelist
    public FiguraVec4 getSkyColor() {
        return VectorsAPI.RGBFromInt(target.getSkyColor());
    }

    @LuaWhitelist
    public FiguraVec4 getFoliageColor() {
        return VectorsAPI.RGBFromInt(target.getFoliageColor());
    }

    @LuaWhitelist
    public FiguraVec4 getWaterColor() {
        return VectorsAPI.RGBFromInt(target.getWaterColor());
    }

    @LuaWhitelist
    public FiguraVec4 getWaterFogColor() {
        return VectorsAPI.RGBFromInt(target.getWaterFogColor());
    }

    @LuaWhitelist
    public FiguraVec4 getFogColor() {
        return VectorsAPI.RGBFromInt(target.getFoliageColor());
    }

    @LuaWhitelist
    public float getDownfall() {
        return target.getDownfall();
    }

    @LuaWhitelist
    public FiguraVec4 getGrassColorAt(int x, int z) {
        return VectorsAPI.RGBFromInt(target.getGrassColorAt(x, z));
    }

    @LuaWhitelist
    public FiguraVec4 getGrassColorAt(FiguraVec2 pos) {
        return VectorsAPI.RGBFromInt(target.getGrassColorAt(pos.x, pos.y));
    }

    @LuaWhitelist
    public FiguraVec4 getGrassColorAt(FiguraVec3 pos) {
        return VectorsAPI.RGBFromInt(target.getGrassColorAt(pos.x, pos.z));
    }

    @LuaWhitelist
    public boolean isColdAt(int x, int y, int z) {
        return target.isCold(new BlockPos(x, y, z));
    }

    @LuaWhitelist
    public boolean isColdAt(FiguraVec3 pos) {
        return target.isCold(pos.getBlockPos());
    }

    @LuaWhitelist
    public boolean isHotAt(int x, int y, int z) {
        return target.isHot(new BlockPos(x, y, z));
    }

    @LuaWhitelist
    public boolean isHotAt(FiguraVec3 pos) {
        return target.isHot(pos.getBlockPos());
    }
}
