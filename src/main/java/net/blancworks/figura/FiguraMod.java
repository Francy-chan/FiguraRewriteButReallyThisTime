package net.blancworks.figura;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.components.script.FiguraLuaManager;
import net.blancworks.figura.avatar.importing.ImporterManager;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.blancworks.figura.serving.FiguraHouse;
import net.blancworks.figura.serving.dealers.FiguraLocalDealer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FiguraMod implements ClientModInitializer {
    public static final String MOD_ID = "figura";
    public static Path gameDir;

    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitializeClient() {
        //Read this from input so we can use it later
        FiguraMod.gameDir = FabricLoader.getInstance().getGameDir();
        //Set up lua stuff
        FiguraLuaManager.init();

        //Init the FiguraHouse, which deals out entities.
        FiguraHouse.init();

        // TODO - REMOVE!!!!

        //Import files from local directory into NBT compound.
        NbtCompound avatarCompound = new NbtCompound();
        ImporterManager.importDirectory(FiguraMod.getLocalAvatarDirectory().resolve("test").toAbsolutePath(), avatarCompound);

        FiguraAvatar localAvatar = new FiguraAvatar();
        FiguraAvatarNbtConverter.readNBT(localAvatar, avatarCompound);

        localAvatar.scriptEnv.render(0);
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
