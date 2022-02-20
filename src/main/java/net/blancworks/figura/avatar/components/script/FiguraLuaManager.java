package net.blancworks.figura.avatar.components.script;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.components.script.api.FiguraAPI;
import net.blancworks.figura.avatar.components.script.api.math.MatricesAPI;
import net.blancworks.figura.avatar.components.script.api.math.VectorsAPI;
import net.blancworks.figura.avatar.components.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.components.script.lua.LuaTable;
import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.NativeSupport;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

public class FiguraLuaManager {
    // -- Variables -- //
    private static String avatarSourceFile;
    private static final HashMap<String, String> resourceFileCache = new HashMap<>();

    // -- Functions -- //
    public static void init() {
        try {
            //Load avatar source file from resources
            avatarSourceFile = new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/avatar.lua").readAllBytes(), StandardCharsets.UTF_8);
            setupNativesForLua();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

        if (isWindows) {
            builder.append(".dll");
        } else if (isMacOS) {
            builder.append(".dylib");
        } else {
            builder.append(".so");
        }

        Path nativesFolder = FiguraMod.gameDir.normalize().resolve("libraries/lua-natives/");

        String targetLib = "/natives/" + builder;
        InputStream libStream = FiguraMod.class.getResourceAsStream(targetLib);
        File f = nativesFolder.resolve(builder.toString()).toFile();

        try {
            if (libStream == null) throw new Exception("Cannot read natives from resources");
            Files.createDirectories(nativesFolder);
            Files.copy(libStream, f.toPath().toAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            FiguraMod.LOGGER.error("Failed to copy Lua natives");
            FiguraMod.LOGGER.error(e);
        }

        NativeSupport.loadLocation = f.getAbsolutePath();
    }


    public static LuaTable loadAvatarContainer(FiguraLuaState state, FiguraScriptEnvironment scriptEnvironment) {

        state.pushJavaFunction(FiguraLuaManager::Print);
        state.setGlobal("f_print");
        state.pushJavaFunction(FiguraLuaManager::LogPrints);
        state.setGlobal("f_logPrints");

        state.pushJavaFunction(FiguraLuaManager::LoadFromResources);
        state.setGlobal("f_loadRes");

        state.pushJavaFunction(s -> {
            //Get & sanitize key from lua
            String key = s.checkString(-1).replace(".lua", "");
            //Get script from script environment
            String value = scriptEnvironment.trueSources.get(key);

            //If no script is found, return nothing.
            if (value == null) return 0;

            //If script is found, put it on the stack
            state.pushString(value);

            //Return 1 argument (the script we loaded)
            return 1;
        });
        state.setGlobal("f_loadScript");


        //Load & call main avatar container
        state.load(avatarSourceFile, "figura_avatar_container");
        state.call(0, 1);

        //Put FiguraAPI into global
        //TODO - Replace with generic API system for other mods/apis!!!
        state.putInGlobalAndScriptEnvironment("figura", new FiguraAPI(scriptEnvironment.ownerAvatar));
        state.putInGlobalAndScriptEnvironment("vectors", new VectorsAPI());
        state.putInGlobalAndScriptEnvironment("matrices", new MatricesAPI());

        //Get the module off the top of the stack, convert it to a map.
        LuaTable module = state.toJavaObject(-1, LuaTable.class);

        //Pop module
        state.pop(1);

        return module;
    }

    // -- Global Functions -- //


    /**
     * Loads a script file from the resources of this mod as a chunk, then drops it on the stack.
     */
    private static int LoadFromResources(LuaState state) {
        try {
            //Get & sanitize key from lua
            String targetFile = state.checkString(1).replace(".lua", "");
            state.pop(1); //Pop string now that we're done with it
            //Load source file from resources (or cache)
            String source = resourceFileCache.computeIfAbsent(String.format("/lua_scripts/%s.lua", targetFile), FiguraLuaManager::loadStringFromResources);

            //Run chunk from source
            state.load(source, targetFile);
            state.call(0, 1);

            //Return 1 argument (the module we loaded from resources)
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Loads a string from a file in resources.
     * NOTE - Does not cache!
     */
    private static String loadStringFromResources(String file) {
        try {
            return new String(FiguraLuaManager.class.getResourceAsStream(file).readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private static final StringBuilder printBuilder = new StringBuilder();

    /**
     * Stores a value to be logged later by LogPrints
     */
    private static int Print(LuaState state) {
        try {
            printBuilder.append(state.toString(-1));
            printBuilder.append(", ");
            return 0;
        } catch (Exception e) {
        }

        return 0;
    }

    //Logs all the print values
    private static int LogPrints(LuaState state){
        //Print out value in string builder
        FiguraMod.LOGGER.info(printBuilder.toString());
        //Clear string builder
        printBuilder.setLength(0);

        return 0;
    }
}
