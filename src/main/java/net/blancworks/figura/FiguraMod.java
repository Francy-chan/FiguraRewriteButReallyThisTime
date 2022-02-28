package net.blancworks.figura;

import net.blancworks.figura.avatar.io.ImporterManager;
import net.blancworks.figura.avatar.texture.FiguraTextureManager;
import net.blancworks.figura.avatar.trust.TrustManager;
import net.blancworks.figura.serving.FiguraHouse;
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

        //Init the texture manager, so we can reload textures where needed.
        FiguraTextureManager.init();

        //Init the FiguraHouse, which deals out entities.
        FiguraHouse.init();

        //Init the trust interaction system
        TrustManager.init();

        //Register fabric events
        ClientTickEvents.END_CLIENT_TICK.register(FiguraMod::tick);

        // TODO - REMOVE!!!!

        ImporterManager.init();
        ImporterManager.updateFoundAvatars();
    }

    public static void tick(MinecraftClient client) {
        ticks++;
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
