package net.blancworks.figura.avatar.script;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.script.lua.modules.FiguraLuaEvent;
import net.blancworks.figura.avatar.script.lua.types.LuaTable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import org.terasology.jnlua.JavaFunction;

import java.util.Map;

/**
 * Handles the lua state of the avatar.
 * <p>
 * Includes all the scripts loaded for an avatar, as well as events and such.
 */
public class FiguraScriptEnvironment {

    // -- Variables -- //

    // Lua State //
    public FiguraLuaState luaState; // The lua state :D
    public Map<String, String> trueSources; // Source files by name
    private Exception luaError = null;

    // Lua values //
    public LuaTable globalTable; // Global table of the lua state.
    public LuaTable scriptSandboxTable; // Script sandbox "Global" table.

    // Helpers //
    private final Object[] returnValues = new Object[16]; //Cache for lua-returned values to be placed into (unused)

    // Events //
    private final FiguraLuaEvent tickEvent = new FiguraLuaEvent(this, "tick");
    private final FiguraLuaEvent renderEvent = new FiguraLuaEvent(this, "render");
    private final FiguraLuaEvent onDamage = new FiguraLuaEvent(this, "onDamage");

    private World lastWorld = null;

    // -- Constructors -- //
    public FiguraScriptEnvironment(Map<String, String> trueSources) {
        this.trueSources = trueSources;
    }

    // -- Functions -- //

    /**
     * Ensures the lua state has been created and has scripts loaded.
     * <p>
     * Returns true if the lua state is valid, returns false otherwise
     */
    public boolean ensureLuaState(FiguraAvatar avatar) {

        //If the lua state has an error, or there are no source files, there's nothing to set up.
        if (luaError != null || trueSources == null || trueSources.size() == 0)
            return false;

        //We've already set up the lua state before!
        if (luaState != null) {
            setupValues();
            return true;
        }

        try {
            //Create lua state
            luaState = new FiguraLuaState();
            luaState.constructFiguraEnvironment(avatar, this);
            setupValues();

            //Get the global table from the lua state, for easy access
            globalTable = luaState.globalTable;
            //Get the script environment table (the sandbox) too.
            scriptSandboxTable = luaState.scriptSandboxTable;

            //Get 'require' from global
            JavaFunction requireFunction = (JavaFunction) globalTable.get("require");

            //Load up all the scripts using require (which has been replaced by figura_modules)
            for (Map.Entry<String, String> entry : trueSources.entrySet()) {
                luaState.pop(luaState.getTop());
                luaState.pushString(entry.getKey());
                //Run require with API name
                requireFunction.invoke(luaState);
            }
        } catch (Exception e) {
            onLuaError(e);

            return false;
        }

        //Lua state was set up! Return true.
        return true;
    }

    private void setupValues(){
        luaState.worldWrapper.overwrite = MinecraftClient.getInstance().world;
    }


    public String getScriptSource(String path) {
        return trueSources.get(path);
    }

    // -- Events -- //

    public synchronized void onLuaError(Exception e) {
        luaError = e;
        FiguraMod.LOGGER.error(e);
    }

    public synchronized void tick(FiguraAvatar avatar) {
        tickEvent.call(avatar, this);
    }

    public synchronized void render(FiguraAvatar avatar, float deltaTime) {
        renderEvent.call(avatar, deltaTime);
    }

    // -- Native -- //
}
