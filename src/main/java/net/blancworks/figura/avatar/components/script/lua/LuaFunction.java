package net.blancworks.figura.avatar.components.script.lua;

import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.LuaValueProxy;

/**
 * Provides an interface for interacting with Lua Functions from java
 */
public class LuaFunction implements LuaValueProxy {
    // -- Variables -- //
    private final FiguraLuaState state;
    private final LuaValueProxy actualProxy;

    // -- Constructors -- //

    //Constructs a LuaFunction from a given FiguraLuaState, creating a reference at the index
    public LuaFunction(FiguraLuaState state, int index){
        this.state = state;

        //Get a proxy for top of stack
        actualProxy = state.getProxy(index);
    }


    // -- Functions -- //

    @Override
    public LuaState getLuaState() {
        return state;
    }

    @Override
    public void pushValue() {
        actualProxy.pushValue();
    }

    /**
     * Calls the lua function with the given arguments, and discards all return values.
     */
    public void call(Object... args) {
        //Put function on stack
        pushValue();

        //Push arguments
        for (Object arg : args)
            state.pushJavaObject(arg);

        //Call function with arg count and 0 returns
        state.call(args.length, 0);
    }

    /**
     * Calls the lua function with the given args, and fills returnValues with as many return values as it can.
     * Returns the number of arguments that lua returned (may be greater than returnValues.length)
     */
    public int call(Object[] returnValues, Object... args){
        //Store how many objects are on the stack, before we do anything.
        int preCount = state.getTop();

        //Push arguments
        for (Object arg : args)
            state.pushJavaObject(arg);

        //Call function with arg count and 0 returns
        state.call(args.length, LuaState.MULTRET);

        //Store how many objects are on the stack, after we called the function
        int postCount = state.getTop();
        //Calculate how many return values there were
        int endCount = postCount - preCount;

        //Put values from lua into returnValues
        for(int i = 0; i < endCount && i < returnValues.length; i++){
            int index = preCount + i + 1; // Index is whatever we started with on the stack + 1, plus the index of what we're accessing
            returnValues[i] = state.toJavaObject(index, Object.class); // Converts all possible objects
        }

        //Pop return values
        state.pop(endCount);

        return endCount;
    }

    /**
     * Calls the lua function with the given args, and returns the first value from lua as the given type.
     */
    public <T> T call(Class<T> type, Object... args){
        //Put function on stack
        pushValue();

        //Push arguments
        for (Object arg : args)
            state.pushJavaObject(arg);

        //Call function with arg count and 0 returns
        state.call(args.length, 1);

        //Convert and pop object
        T ret = state.toJavaObject(-1, type);
        state.pop(1);

        //Return object
        return ret;
    }
}
