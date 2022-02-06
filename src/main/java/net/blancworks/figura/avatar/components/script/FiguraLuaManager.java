package net.blancworks.figura.avatar.components.script;

import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.components.script.reflector.FiguraJavaReflector;
import org.terasology.jnlua.JavaFunction;
import org.terasology.jnlua.LuaState;

import java.nio.charset.StandardCharsets;

public class FiguraLuaManager {
    // -- Variables -- //
    private static String avatarSourceFile;

    // -- Functions -- //
    public static void init() {
        try {
            avatarSourceFile = new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/avatar.lua").readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupLuaState(LuaState state, FiguraScriptEnvironment scriptEnvironment) {

        // -- Global figura functions -- //
        state.pushJavaFunction(FiguraLuaManager::LoadFromResources);
        state.setGlobal("f_loadRes");

        state.pushJavaFunction(FiguraLuaManager::Print);
        state.setGlobal("print");

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

    private static int LoadFromResources(LuaState state) {
        try {
            //Get & sanitize key from lua
            String targetFile = state.checkString(1).replace(".lua", "");
            String source = new String(FiguraLuaManager.class.getResourceAsStream(String.format("/lua_scripts/%s.lua", targetFile)).readAllBytes(), StandardCharsets.UTF_8);

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

    private static int Print(LuaState state) {
        try {
            System.out.println(state.toString(-1));
            return 0;
        } catch (Exception e) {
        }

        return 0;
    }
}
