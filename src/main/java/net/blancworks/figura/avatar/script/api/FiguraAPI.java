package net.blancworks.figura.avatar.script.api;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.model.FiguraModelPart;
import net.blancworks.figura.avatar.script.api.general.MetaAPI;
import net.blancworks.figura.avatar.script.lua.reflector.LuaWhitelist;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;

/**
 * The Figura API. Holds all the global values related to Figura and the data it adds to the game.
 */
public class FiguraAPI extends ObjectWrapper<FiguraAPI> {

    @LuaWhitelist
    public final String __VERSION;

    // -- Variables -- //
    /**
     * Reference to all of the models within the current avatar.
     */
    @LuaWhitelist
    public final FiguraModelPart models;

    @LuaWhitelist
    public final MetaAPI metaAPI;

    @LuaWhitelist
    public final NbtCompound testNbtCompound = new NbtCompound();

    // -- Constructors -- //
    public FiguraAPI(FiguraAvatar avatar){
        __VERSION = "0.1.0";
        models = avatar.getRoot();
        metaAPI = new MetaAPI(avatar);
    }
}
