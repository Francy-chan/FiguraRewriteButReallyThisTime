package net.blancworks.figura.avatar.components.script.lua;

import net.blancworks.figura.avatar.components.script.FiguraScriptEnvironment;

public class LuaEvent {
    // -- Variables -- //
    private FiguraScriptEnvironment environment;
    private String eventName;

    private LuaFunction eventFunction;

    // -- Constructors -- //

    public LuaEvent(FiguraScriptEnvironment environment, String eventName) {
        this.environment = environment;
        this.eventName = eventName;
    }

    // -- Functions -- //

    /**
     * Sets up the event, if needed.
     */
    private boolean setup() {
        //If the lua state isn't valid, setup can't be completed, and therefore fails.
        if(!environment.ensureLuaState()) return false;

        //If there's no event function, generate one.
        if (eventFunction == null) {
            //If there's no constructEventFunction for some reason, we can't generate an event function.
            if (environment.constructEventFunction == null) return false;

            //Construct event function :D
            eventFunction = environment.constructEventFunction.call(LuaFunction.class, eventName);
        }

        return true;
    }

    /**
     * Calls this event.
     * Events can't have return arguments, so this is luckily pretty simple.
     */
    public void call(Object... args) {
        if(setup())
            eventFunction.call(args);
    }

}