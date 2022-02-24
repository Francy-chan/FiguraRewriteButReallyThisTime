package net.blancworks.figura.avatar.script.lua.modules;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;

public class FiguraLuaEvent {
    // -- Variables -- //
    private final FiguraScriptEnvironment environment;
    private final String eventName;

    private FiguraLuaModuleManager.LuaEventGroup eventGroup;

    // -- Constructors -- //

    public FiguraLuaEvent(FiguraScriptEnvironment environment, String eventName) {
        this.environment = environment;
        this.eventName = eventName;
    }

    // -- Functions -- //

    /**
     * Sets up the event, if needed.
     */
    private boolean setup(FiguraAvatar avatar) {
        //If the lua state isn't valid, setup can't be completed, and therefore fails.
        if(!environment.ensureLuaState(avatar)) return false;

        //If there's no event function, generate one.
        if (eventGroup == null)
            eventGroup = environment.luaState.moduleManager.getEvent(eventName);

        // TODO - Set instruction limit here based on trust setting

        return true;
    }

    /**
     * Calls this event.
     * Events can't have return arguments, so this is luckily pretty simple.
     */
    public void call(FiguraAvatar avatar, Object... args) {
        try {
            if (setup(avatar))
                eventGroup.run(args);
        } catch (Exception e){
            environment.onLuaError(e);
        }
    }
}
