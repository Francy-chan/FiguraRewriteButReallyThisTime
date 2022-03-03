package net.blancworks.figura.avatar.script.lua;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.blancworks.figura.avatar.script.api.wrappers.world.WorldWrapper;
import net.blancworks.figura.avatar.script.api.wrappers.world.entity.LivingEntityWrapper;
import net.blancworks.figura.avatar.script.lua.converter.FiguraJavaConverter;
import net.blancworks.figura.avatar.script.lua.modules.FiguraLuaModuleManager;
import net.blancworks.figura.avatar.script.lua.reflector.FiguraJavaReflector;
import net.blancworks.figura.avatar.script.lua.types.LuaTable;
import net.blancworks.figura.utils.external.APIFactory;
import net.minecraft.entity.player.PlayerEntity;
import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.LuaState53;
import org.terasology.jnlua.NativeSupport;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

//Custom LuaState for Figura that contains a bunch of helper functions
public class FiguraLuaState extends LuaState53 implements Closeable {

    // -- Variables -- //
    private static HashMap<String, APIFactory> extensionAPIs;
    private static HashMap<String, APIFactory> globalAPIs;
    private static final String avatarSourceFile;

    static {
        String s;
        try {
            s = new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/avatar_module.lua").readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            s = "";
            FiguraMod.LOGGER.error(e);
        }
        avatarSourceFile = s;

        setupNativesForLua();
    }


    public FiguraLuaModuleManager moduleManager;


    /**
     * This is the true lua global table.
     */
    public LuaTable globalTable;
    /**
     * This table is the global table, for scripts, since they're sandboxed.
     */
    public LuaTable scriptSandboxTable;

    /**
     * The table that all extensions go into. Located at `_G.extensions` in lua.
     */
    public LuaTable extensionsTable;


    /**
     * The table returned by the avatar module.
     */
    public LuaTable avatarModuleTable;

    /**
     * The sandbox library we use for, well, sandboxing.
     */
    public LuaTable sandboxModule;


    public WorldWrapper worldWrapper;
    public LivingEntityWrapper<PlayerEntity> playerWrapper;

    /**
     * The size of the built-in APIs, in bytes.
     */
    private int builtinApiSize;

    // -- Constructors -- //

    public FiguraLuaState() {
        //Always initialize with a memory limit. A L W A Y S.
        this(1024 * 64);
    }

    public FiguraLuaState(int memory) {
        super(memory);

        //Set up GC
        gc(LuaState.GcAction.SETPAUSE, 100);
        gc(LuaState.GcAction.SETSTEPMUL, 400);

        //Set custom reflector and converter
        setJavaReflector(new FiguraJavaReflector());
        setConverter(new FiguraJavaConverter());

        //Open the standard libraries (they'll only be accessible by the avatar module!)
        openLibs();

        //Store global table for reference later
        globalTable = getGlobalObject("_G", LuaTable.class);

        //Put sandbox table into global, then store it.
        newTable();
        setGlobal("scriptSandbox");
        scriptSandboxTable = (LuaTable) globalTable.get("scriptSandbox");

        //Put an extensions table into global, then store it.
        newTable();
        setGlobal("extensions");
        extensionsTable = (LuaTable) globalTable.get("extensions");
    }

    public static void setAPIs(HashMap<String, APIFactory> extensions, HashMap<String, APIFactory> globals) {
        if (extensionAPIs != null)
            return;
        extensionAPIs = extensions;
        globalAPIs = globals;
    }

    /**
     * Constructs all the values in lua required for the global scripting environment.
     */
    public void constructFiguraEnvironment(FiguraAvatar avatar, FiguraScriptEnvironment scriptEnvironment) {

        pushJavaFunction(this::loadStringCustom);
        setGlobal("f_loadString");

        //Load the `sandbox` script into global
        try {
            var sandboxSource = new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/sandbox.lua").readAllBytes(), StandardCharsets.UTF_8);
            load(sandboxSource, "sandbox");
            call(0, 1);
            sandboxModule = toJavaObject(-1, LuaTable.class);
            setGlobal("sandbox");
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }

        //Load up the module manager.
        moduleManager = new FiguraLuaModuleManager(this, scriptEnvironment::getScriptSource);

        //Print function helpers.
        pushJavaFunction(FiguraLuaState::print);
        setGlobal("f_print");
        pushJavaFunction(FiguraLuaState::logPrints);
        setGlobal("f_logPrints");

        //Creates all the APIs generated by extensions (and figura itself)
        createAPIs(avatar);

        //Load up the main avatar container script
        load(avatarSourceFile, "figura_avatar_container");
        call(0, 1);
        avatarModuleTable = toJavaObject(-1, LuaTable.class);
        pop(1);

        moduleManager.setupInstructionLimitFunctions(this);

        //Full GC sweep, then calculate used memory.
        gc(LuaState.GcAction.COLLECT, 0);
        builtinApiSize = (1024 * 64) - getFreeMemory();
    }

    // -- Functions -- //

    /**
     * Increases the maximum memory of the lua space to the cap,
     */
    public void setMaxMemory(int maxMemory) {
        gc(LuaState.GcAction.COLLECT, 0); //GC just to be safe
        super.setTotalMemory(builtinApiSize + maxMemory); //Set the maximum memory size of this lua state.
    }


    public void createAPIs(FiguraAvatar avatar) {
        for (var entry : globalAPIs.entrySet())
            putInGlobalAndScriptEnvironment(entry.getKey(), entry.getValue().run(avatar));

        for (var entry : extensionAPIs.entrySet())
            extensionsTable.put(entry.getKey(), entry.getValue().run(avatar));
    }

    public void putInGlobalAndScriptEnvironment(String key, Object obj) {
        globalTable.put(key, obj);
        scriptSandboxTable.put(key, obj);
    }

    /**
     * Gets a global variable using toJavaObject
     */
    public <T> T getGlobalObject(String key, Class<T> clazz) {
        getGlobal(key);
        T obj = toJavaObject(-1, clazz);
        pop(1);

        return obj;
    }

    @Override
    public synchronized void close() {
        super.close();
    }


    // -- Lua Functions -- //

    private int loadStringCustom(LuaState state) {
        state.load(state.checkString(1), state.checkString(2));
        return 1;
    }

    // Print //
    private static final StringBuilder printBuilder = new StringBuilder();

    /**
     * Stores a value to be logged later by LogPrints
     */
    private static int print(LuaState state) {
        try {
            printBuilder.append(state.toString(-1));
            printBuilder.append(", ");
            return 0;
        } catch (Exception ignored) {
        }

        return 0;
    }

    /**
     * Logs all the print values currently stored
     */
    private static int logPrints(LuaState state) {
        //Print out value in string builder
        FiguraMod.LOGGER.info(printBuilder.toString());
        //Clear string builder
        printBuilder.setLength(0);

        return 0;
    }

    // -- Util -- //

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
}
