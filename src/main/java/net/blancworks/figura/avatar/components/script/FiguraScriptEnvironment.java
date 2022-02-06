package net.blancworks.figura.avatar.components.script;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.FiguraNativeObject;
import net.blancworks.figura.avatar.components.FiguraAvatarComponent;
import net.blancworks.figura.avatar.components.script.api.FiguraAPI;
import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
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

    public LuaState53 luaState;
    public Map<String, String> trueSources;
    private int avatarContainerID = -1;
    private final LuaEvent tickEvent = new LuaEvent("tick");
    private final LuaEvent renderEvent = new LuaEvent("render");

    // -- Constructors -- //
    public FiguraScriptEnvironment(FiguraAvatar ownerAvatar) {
        super(ownerAvatar);
    }

    // -- Functions -- //

    /**
     * Ensures the lua state has been created and has scripts loaded.
     */
    public void ensureLuaState() {
        if (luaState != null || trueSources == null || trueSources.size() == 0)
            return;

        luaState = new LuaState53();
        luaState.openLibs();

        luaState.setJavaReflector(new FiguraJavaReflector());

        //Track this native object to clean up later.
        ownerAvatar.trackNativeObject(new LuaEnvironmentWrapper(luaState));

        try {

            luaState.pushJavaObject(new FiguraAPI(ownerAvatar));
            luaState.setGlobal("figura");

            //Set up the lua state
            //When this returns, the avatar container table is the top of the stack
            FiguraLuaManager.setupLuaState(luaState, this);
            // Hold a reference to the avatar container
            avatarContainerID = luaState.ref(luaState.REGISTRYINDEX);

            //Load up all the scripts
            for (Map.Entry<String, String> entry : trueSources.entrySet()) {
                String apiName = entry.getKey();

                luaState.getGlobal("require");
                luaState.pushString(apiName);
                luaState.call(1, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        render(0);
    }

    //Gets a value from a reference, and puts it on the stack.
    private void getRefValue(int id) {
        luaState.rawGet(luaState.REGISTRYINDEX, id);
    }

    // -- Events --

    public void tick() {
        ensureLuaState();

        tickEvent.call();
    }

    public void render(float deltaTime) {
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
            getRefValue(avatarContainerID);

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
            if(setup()) {
                //Get function onto the stack
                getRefValue(refID);

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
