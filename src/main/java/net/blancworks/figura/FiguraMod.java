package net.blancworks.figura;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.io.ImporterManager;
import net.blancworks.figura.avatar.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.script.lua.reflector.FiguraJavaReflector;
import net.blancworks.figura.avatar.script.lua.reflector.wrappers.ObjectWrapper;
import net.blancworks.figura.avatar.texture.FiguraTextureManager;
import net.blancworks.figura.config.ConfigManager;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.trust.TrustManager;
import net.blancworks.figura.utils.external.APIFactory;
import net.blancworks.figura.utils.external.FiguraExtension;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Supplier;

public class FiguraMod implements ClientModInitializer {
    public static final String MOD_ID = "figura";
    public static Path gameDir;

    public static final Logger LOGGER = LogManager.getLogger();
    public static final boolean CHEESE_DAY = LocalDate.now().getDayOfMonth() == 1 && LocalDate.now().getMonthValue() == 4;

    public static int ticks = 0;

    @Override
    public void onInitializeClient() {
        //Read this from input so we can use it later
        FiguraMod.gameDir = FabricLoader.getInstance().getGameDir();

        //Init the config manager
        ConfigManager.init();

        //Init the texture manager, so we can reload textures where needed.
        FiguraTextureManager.init();

        //Init the FiguraHouse, which deals out entities.
        FiguraHouse.init();

        //Init the trust interaction system
        TrustManager.init();

        //Register fabric events
        ClientTickEvents.END_CLIENT_TICK.register(FiguraMod::tick);

        //Process extensions using entrypoints
        processExtensions();

        ImporterManager.init();
        ImporterManager.updateFoundAvatars();
    }

    public static void tick(MinecraftClient client) {
        ticks++;
    }

    /**
     * Processes entrypoints and turns them into extensions for the API and such.
     */
    public void processExtensions() {
        var wrapperBuilder = new ImmutableMap.Builder<Class<?>, Supplier<ObjectWrapper<?>>>(); // Custom ObjectWrappers
        var extensionAPIs = new HashMap<String, APIFactory>();


        FabricLoader.getInstance().getEntrypointContainers("figura", FiguraExtension.class).forEach(entrypoint -> {
            var extension = entrypoint.getEntrypoint();
            extension.initialize();
            // Note that there's no priority system here, so this is basically random based on load order for mods.
            // All mods should have unique APIs, preferably.
            // TODO - Also probably enable the ability for multiple ObjectWrappers per type, I think.

            //Put all custom wrappers.
            wrapperBuilder.putAll(extension.customWrappers);

            //Put all extension apis in.
            extensionAPIs.putAll(extension.apiFactories);
        });

        //Load up built-in API
        //Doesn't use entrypoints, because it needs to always go last.
        BuiltinFiguraExtension builtin = new BuiltinFiguraExtension();
        wrapperBuilder.putAll(builtin.customWrappers);

        //Set out all the values gotten from entrypoints.
        FiguraJavaReflector.setWrappers(wrapperBuilder.build());
        FiguraLuaState.setAPIs(extensionAPIs, builtin.apiFactories);
    }

    // -- Helper Functions --

    // - Directory -
    public static Path getModDirectory() {
        Path p = gameDir.normalize().resolve(MOD_ID);
        try {
            Files.createDirectories(p);
        } catch (Exception e) {
            LOGGER.error("Failed to create the main Figura directory");
            LOGGER.error(e);
        }

        return p;
    }

    public static Path getCacheDirectory() {
        Path p = getModDirectory().resolve("cache");
        try {
            Files.createDirectories(p);
        } catch (IOException e) {
            LOGGER.error("Failed to create cache directory");
            LOGGER.error(e);
        }

        return p;
    }

    public static Path getLocalAvatarDirectory() {
        Path p = getModDirectory().resolve("avatars");
        try {
            Files.createDirectories(p);
        } catch (IOException e) {
            LOGGER.error("Failed to create avatar directory");
            LOGGER.error(e);
        }

        return p;
    }
}
