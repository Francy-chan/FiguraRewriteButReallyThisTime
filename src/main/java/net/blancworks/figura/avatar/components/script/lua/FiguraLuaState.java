package net.blancworks.figura.avatar.components.script.lua;

import net.blancworks.figura.avatar.components.script.lua.converter.FiguraJavaConverter;
import net.blancworks.figura.avatar.components.script.lua.reflector.FiguraJavaReflector;
import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.LuaState53;

import java.util.Map;

//Custom LuaState for Figura that contains a bunch of helper functions
public class FiguraLuaState extends LuaState53 {

    // -- Variables -- //
    public final LuaTable globalTable;

    // -- Constructors -- //

    public FiguraLuaState(){
        //Always initialize with a memory limit. A L W A Y S.
        this(1024 * 64);
    }

    public FiguraLuaState(int memory){
        super(memory);

        //Set up GC
        gc(LuaState.GcAction.SETPAUSE, 100);
        gc(LuaState.GcAction.SETSTEPMUL, 400);

        //Set custom reflector that uses ObjectWrappers :D
        setJavaReflector(new FiguraJavaReflector());
        setConverter(new FiguraJavaConverter());

        //Open the standard libraries (they'll only be accessible by the avatar module!)
        openLibs();

        //Store global table for reference later
        globalTable = getGlobalObject("_G", LuaTable.class);
    }

    // -- Functions -- //

    /**
     * Gets a global variable using toJavaObject
     */
    public <T> T getGlobalObject(String key, Class<T> clazz){
        getGlobal(key);
        T obj = toJavaObject(-1, clazz);
        pop(1);

        return obj;
    }

    /**
     * Calls a function that's in the global table
     */
    public int callFunctionGlobal(String globalName, Object... args) {
        getGlobal(globalName);

        return callFunction(args);
    }

    /**
     * Calls the function on top of the stack
     */
    public int callFunction(Object[] returnValues, Object... args) {
        //Verify value is a function before calling it.
        if (!isFunction(-1)) return 0;

        //Stack size before function has been called (excluding the function itself)
        int startCount = getTop() - 1;

        for (Object o : args) pushJavaObject(o);

        //Call lua function
        call(args.length, LuaState.MULTRET);

        //Calculate how many objects there are on the stack after we called the function
        int endCount = getTop() - startCount;

        //Put values from lua into returnValues
        for(int i = 0; i < endCount && i < returnValues.length; i++){
            int index = startCount + i + 1; // Index is whatever we started with on the stack + 1, plus the index of what we're accessing
            returnValues[i] = toJavaObject(index, Object.class); // Converts all possible objects
        }

        return endCount;
    }

    // -- Nested Types -- //

}
