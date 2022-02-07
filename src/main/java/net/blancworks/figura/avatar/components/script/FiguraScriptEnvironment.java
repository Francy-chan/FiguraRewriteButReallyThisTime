package net.blancworks.figura.avatar.components.script;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.FiguraNativeObject;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.blancworks.figura.avatar.components.script.api.FiguraAPI;
import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import org.terasology.jnlua.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles the lua state of the avatar.
 */
public class FiguraScriptEnvironment extends FiguraAvatarComponent<NbtCompound> {

    // -- Variables -- //

    // Lua State //
    public LuaState53 luaState; // The lua state :D
    public Map<String, String> trueSources; // Source files by name
    private int avatarModuleRefID = -1; // Location in registry that the main avatar container is located at

    private Exception luaError = null;

    // Helpers //
    private final Object[] returnValues = new Object[16]; //Cache for lua-returned values to be placed into (unused)

    // Events //
    private final LuaEvent tickEvent = new LuaEvent("tick");
    private final LuaEvent renderEvent = new LuaEvent("render");
    private final LuaEvent onDamage = new LuaEvent("onDamage");

    // -- Constructors -- //
    public FiguraScriptEnvironment(FiguraAvatar ownerAvatar) {
        super(ownerAvatar);
    }

    // -- Functions -- //

    /**
     * Ensures the lua state has been created and has scripts loaded.
     */
    public boolean ensureLuaState() {

        if (luaError != null || luaState != null || trueSources == null || trueSources.size() == 0)
            return false;

        //64kb memory to start
        luaState = new LuaState53(1024 * 64);
        //Track this native object to clean up later.
        ownerAvatar.trackNativeObject(new LuaEnvironmentWrapper(luaState));

        luaState.gc(LuaState.GcAction.SETPAUSE, 100);
        luaState.gc(LuaState.GcAction.SETSTEPMUL, 400);

        //Set custom reflector that uses ObjectWrappers :D
        luaState.setJavaReflector(new FiguraJavaReflector());

        //Open the standard libraries (they'll only be accessible by the avatar module!)
        luaState.openLibs();

        try {
            //Set up the lua state
            //When this returns, the avatar container table is the top of the stack
            FiguraLuaManager.setupLuaState(luaState, this);
            // Hold a reference to the avatar container
            avatarModuleRefID = luaState.ref(luaState.REGISTRYINDEX);

            //Load up all the scripts using require (which has been replaced by figura_modules)
            for (Map.Entry<String, String> entry : trueSources.entrySet()) {
                String apiName = entry.getKey();

                int rCount = callFunctionGlobal("require", apiName);
                luaState.pop(rCount); //Pop returns off of the stack so it's clean
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //full GC sweep to clean up anything the API has laying around
        luaState.gc(LuaState.GcAction.COLLECT, 0);

        //Calculate how much memory the API is using right now
        int freeMemory = luaState.getFreeMemory();
        int usedMemory = (1024 * 64) - freeMemory;

        //Set the total memory to whatever the API is using + 64kb
        luaState.setTotalMemory(usedMemory + (1024 * 64));

        return true;
    }

    //Gets a value from a reference, and puts it on the stack.
    private void getRefValue(int id) {
        luaState.rawGet(luaState.REGISTRYINDEX, id);
    }


    // -- Helpers -- //

    /**
     * Calls a function on the avatar module.
     */
    public int callFunctionAvatar(String functionName, Object... args) {
        //Get avatar module on the stack
        getRefValue(avatarModuleRefID);
        //Put function name on the stack
        luaState.pushString(functionName);

        //Get `functionName` (top of stack) from table at t-2 (the avatar module)
        luaState.getTable(-2);

        //Call top value.
        return callFunction(args);
    }

    /**
     * Calls a function that's in the global table
     */
    public int callFunctionGlobal(String globalName, Object... args) {
        luaState.getGlobal(globalName);

        return callFunction(args);
    }

    /**
     * Calls the function on top of the stack
     */
    public int callFunction(Object... args) {
        //Verify value is a function before calling it.
        if (!luaState.isFunction(-1)) return 0;

        //Stack size before function has been called (excluding the function itself)
        int startCount = luaState.getTop() - 1;

        for (Object o : args) luaState.pushJavaObject(o);

        //Call lua function
        luaState.call(args.length, LuaState.MULTRET);

        int endCount = luaState.getTop() - startCount;

        //TODO - put return values in returnValues

        return endCount;
    }

    // -- Events --

    public synchronized void tick() {
        ensureLuaState();

        tickEvent.call();
    }

    public synchronized void render(float deltaTime) {
        ensureLuaState();

        renderEvent.call(deltaTime);
    }

    /**
     * Provides an easy way to use the Events API from java (for you, fran!)
     */
    private class LuaEvent {
        private boolean tried = false;
        private Integer refID = null;
        private final String key;

        public LuaEvent(String key) {
            this.key = key;
        }

        private void getRef() {
            //Get the avatar container
            getRefValue(avatarModuleRefID);

            //Get function 'constructEventFunction' from avatar container
            luaState.pushString("constructEventFunction");
            luaState.getTable(-2);

            //Call 'constructEventFunction' using key as the event name
            luaState.pushString(key);
            luaState.call(1, 1);

            //Make a reference to the function returned by 'constructEventFunction'
            refID = luaState.ref(luaState.REGISTRYINDEX);

            //Pop avatar container table
            luaState.pop(1);
        }

        private boolean setup() {
            //return false if there is no lua state
            if (luaState == null)
                return false;

            //If first time setting up, get reference to event (if any)
            if (!tried) {
                tried = true;
                getRef();
            }

            //If no event was found, return false.
            if (refID == null) return false;

            //Gets the function using the reference index.
            getRefValue(refID);
            return true;
        }

        public void call(Object... args) {
            if (setup()) {

                //Push arguments
                for (Object arg : args)
                    luaState.pushJavaObject(arg);

                //Call function with arg count and 0 returns
                luaState.call(args.length, 0);
            }
        }
    }

    // -- IO --
    @Override
    public void readFromNBT(@NotNull NbtCompound tag) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key : tag.getKeys()) {
            byte[] stringData = tag.getByteArray(key);
            builder.put(key, new String(stringData, StandardCharsets.UTF_8));
        }

        trueSources = builder.build();
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
