package net.blancworks.figura.avatar.components.script;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.components.script.api.FiguraAPI;
import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.LuaState;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FiguraLuaManager {
    // -- Variables -- //
    private static String avatarSourceFile;
    private static final HashMap<String, String> resourceFileCache = new HashMap<>();

    // -- Functions -- //
    public static void init() {
        try {
            //Load avatar source file from resources
            avatarSourceFile = new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/avatar.lua").readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupLuaState(LuaState state, FiguraScriptEnvironment scriptEnvironment) {

        state.pushJavaFunction(FiguraLuaManager::Print);
        state.setGlobal("print");

        //Put FiguraAPI into global
        //TODO - Replace with generic API system for other mods/apis!!!
        state.pushJavaObject(new FiguraAPI(scriptEnvironment.ownerAvatar));
        state.setGlobal("figura");

        state.pushJavaFunction(FiguraLuaManager::LoadFromResources);
        state.setGlobal("f_loadRes");

        state.pushJavaFunction(s -> {
            //Get & sanitize key from lua
            String key = s.checkString(-1).replace(".lua", "");
            //Get script from script environment
            String value = scriptEnvironment.trueSources.get(key);

            //If no script is found, return nothing.
            if (value == null) return 0;

            //If script is found, put it on the stack
            state.pushString(value);

            //Return 1 argument (the script we loaded)
            return 1;
        });
        state.setGlobal("f_loadScript");


        //Load & call main avatar environment
        state.load(avatarSourceFile, "figura_avatar_init");
        state.call(0, 1);
    }

    // -- Global Functions -- //


    /**
     * Loads a script file from the resources of this mod as a chunk, then drops it on the stack.
     */
    private static int LoadFromResources(LuaState state) {
        try {
            //Get & sanitize key from lua
            String targetFile = state.checkString(1).replace(".lua", "");
            state.pop(1); //Pop string now that we're done with it
            //Load source file from resources (or cache)
            String source = resourceFileCache.computeIfAbsent(String.format("/lua_scripts/%s.lua", targetFile), FiguraLuaManager::loadStringFromResources);

            //Run chunk from source
            state.load(source, targetFile);
            state.call(0, 1);

            //Return 1 argument (the module we loaded from resources)
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Loads a string from a file in resources.
     * NOTE - Does not cache!
     */
    private static String loadStringFromResources(String file) {
        try {
            return new String(FiguraLuaManager.class.getResourceAsStream(file).readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * Print a value from the lua state.
     */
    private static int Print(LuaState state) {

        //TODO - Improve to work on all lua objects
        //       Also, make it work with multiple arguments
        try {
            System.out.println(state.toString(-1));
            state.pop(1);
            return 0;
        } catch (Exception e) {
        }

        return 0;
    }
}
