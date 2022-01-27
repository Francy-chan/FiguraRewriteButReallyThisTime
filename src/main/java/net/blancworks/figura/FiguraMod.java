package net.blancworks.figura;

import com.google.gson.Gson;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.importing.ImporterManager;
import net.blancworks.figura.avatar.reader.FiguraAvatarNbtConverter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import org.terasology.jnlua.NativeSupport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FiguraMod implements ClientModInitializer {
    public static final String MOD_ID = "figura";
    public static final Gson GSON = new Gson();
    public static Path gameDir;


    @Override
    public void onInitializeClient() {
        //Read this from input so we can use it later
        FiguraMod.gameDir = FabricLoader.getInstance().getGameDir();
        //Set up lua native libraries
        setupNativesForLua();

        //Import files from local directory into NBT compound.
        NbtCompound avatarCompound = new NbtCompound();
        ImporterManager.importDirectory(FiguraMod.getLocalAvatarDirectory().resolve("test").toAbsolutePath(), avatarCompound);

        FiguraAvatar localAvatar = new FiguraAvatar();
        FiguraAvatarNbtConverter.readNBT(localAvatar, avatarCompound);
    }

    // -- Helper Functions --

    // - Lua -

    /**
     * Figures out the OS and copies the appropriate lua native binaries into a path, then loads them up
     * so that JNLua has access to them.
     */
    public static void setupNativesForLua() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        boolean isMacOS = System.getProperty("os.name").toLowerCase().contains("mac");
        StringBuilder builder = new StringBuilder(isWindows ? "libjnlua-" : "jnlua-");
        builder.append("5.3-");
        if (isWindows) {
            builder.append("windows-");
        } else if (isMacOS) {
            builder.append("mac-");
        } else {
            builder.append("linux-");
        }

        if (System.getProperty("os.arch").endsWith("64")) {
            builder.append("amd64");
        } else {
            builder.append("i686");
        }

        String ext = "";
        if (isWindows) {
            ext = ".dll";
        } else if (isMacOS) {
            ext = ".dylib";
        } else {
            ext = ".so";
        }

        String targetLib = "/natives/" + builder + ext;
        InputStream libStream = FiguraMod.class.getResourceAsStream(targetLib);
        File f = new File(builder + ext);

        try {
            Files.copy(libStream, f.toPath().toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException var9) {
            var9.printStackTrace();
        }

        NativeSupport.loadLocation = f.getAbsolutePath();
    }

    // - Directory -
    public static Path getModDirectory() {
        Path p = gameDir.normalize().resolve("figura");
        try {
            Files.createDirectory(p);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return p;
    }

    public static Path getCacheDirectory() {
        Path p = getModDirectory().resolve("cache");
        try {
            Files.createDirectory(p);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return p;
    }

    public static Path getLocalAvatarDirectory() {
        Path p = getModDirectory().resolve("avatars");
        try {
            Files.createDirectory(p);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return p;
    }
}
