package net.blancworks.figura.avatar.components.script;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.FiguraNativeObject;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.blancworks.figura.avatar.components.script.api.math.matrix.MatricesAPI;
import net.blancworks.figura.avatar.components.script.api.math.vector.VectorsAPI;
import net.blancworks.figura.avatar.components.script.lua.FiguraLuaState;
import net.blancworks.figura.avatar.components.script.lua.LuaEvent;
import net.blancworks.figura.avatar.components.script.lua.LuaFunction;
import net.blancworks.figura.avatar.components.script.lua.LuaTable;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.terasology.jnlua.LuaState;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles the lua state of the avatar.
 */
public class FiguraScriptEnvironment extends FiguraAvatarComponent<NbtCompound> {

    // -- Variables -- //

    // Lua State //
    public FiguraLuaState luaState; // The lua state :D
    public Map<String, String> trueSources; // Source files by name
    private Exception luaError = null;

    // Lua values //
    public LuaTable globalTable; // Global table of the lua state.
    public LuaTable scriptEnvironmentTable; // Script sandbox "Global" table.
    public LuaTable avatarContainerModule; // Avatar container module table from the lua state.
    public LuaFunction constructEventFunction; //Used to construct events, cached for speedies and memory


    // Helpers //
    private final Object[] returnValues = new Object[16]; //Cache for lua-returned values to be placed into (unused)

    // Events //
    private final LuaEvent tickEvent = new LuaEvent(this, "tick");
    private final LuaEvent renderEvent = new LuaEvent(this, "render");
    private final LuaEvent onDamage = new LuaEvent(this, "onDamage");

    // -- Constructors -- //
    public FiguraScriptEnvironment(FiguraAvatar ownerAvatar) {
        super(ownerAvatar);
    }

    // -- Functions -- //

    /**
     * Ensures the lua state has been created and has scripts loaded.
     * <p>
     * Returns true if the lua state is valid, returns false otherwise
     */
    public boolean ensureLuaState() {
        //If the lua state has an error, or there are no source files, there's nothing to set up.
        if (luaError != null || trueSources == null || trueSources.size() == 0)
            return false;

        //We've already set up the lua state before!
        if (luaState != null)
            return true;

        //Create lua state
        luaState = new FiguraLuaState();
        //Track this native object to clean up later.
        ownerAvatar.trackNativeObject(new LuaEnvironmentWrapper(luaState));

        //Get the global table from the lua state, for easy access
        globalTable = luaState.globalTable;

        scriptEnvironmentTable = luaState.scriptEnvironmentTable;

        try {
            //Load the main avatar container script
            avatarContainerModule = FiguraLuaManager.loadAvatarContainer(luaState, this);
            //Cache constructEventFunction for later use by events
            constructEventFunction = avatarContainerModule.getLuaFunction("constructEventFunction");

            //Get 'require' from global
            LuaFunction requireFunction = globalTable.getLuaFunction("require");

            //Load up all the scripts using require (which has been replaced by figura_modules)
            for (Map.Entry<String, String> entry : trueSources.entrySet()) {
                //Run require with API name
                requireFunction.call(entry.getKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Memory Setup //

        //full GC sweep to clean up anything the API has laying around
        luaState.gc(LuaState.GcAction.COLLECT, 0);

        //Calculate how much memory the API is using right now
        int freeMemory = luaState.getFreeMemory();
        int usedMemory = (1024 * 64) - freeMemory;

        //Set the total memory to whatever the API is using + 64kb (so that scripts actually have 64kb of memory on top of the API)
        luaState.setTotalMemory(usedMemory + (1024 * 64));

        //Lua state was set up! Return true.
        return true;
    }

    // IO //
    @Override
    public void readFromNBT(@NotNull NbtCompound tag) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key : tag.getKeys()) {
            byte[] stringData = tag.getByteArray(key);
            builder.put(key, new String(stringData, StandardCharsets.UTF_8));
        }

        trueSources = builder.build();
    }

    // -- Events -- //

    public synchronized void tick() {
        tickEvent.call();
    }

    public synchronized void render(float deltaTime) {
        renderEvent.call(deltaTime);
    }


    // -- Native -- //

    /**
     * Wraps the lua state so we're not holding a direct reference to the avatar
     */
    private static class LuaEnvironmentWrapper implements FiguraNativeObject {
        public LuaState state;

        public LuaEnvironmentWrapper(LuaState state) {
            this.state = state;
        }

        @Override
        public void destroy() {
            state.close();
        }
    }
}
