package net.blancworks.figura.avatar.script.lua.modules;

import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.blancworks.figura.trust.TrustContainer;

public class FiguraLuaEvent {
    // -- Variables -- //
    private final FiguraScriptEnvironment environment;
    private final String eventName;
    private final TrustContainer.Trust trustSetting;

    private FiguraLuaModuleManager.LuaEventGroup eventGroup;

    // -- Constructors -- //

    public FiguraLuaEvent(FiguraScriptEnvironment environment, String eventName, TrustContainer.Trust trustSetting) {
        this.environment = environment;
        this.eventName = eventName;
        this.trustSetting = trustSetting;
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

        if(trustSetting != null)
            eventGroup.instructionLimit = avatar.trustContainer == null ? 2048 : avatar.trustContainer.get(trustSetting);

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
