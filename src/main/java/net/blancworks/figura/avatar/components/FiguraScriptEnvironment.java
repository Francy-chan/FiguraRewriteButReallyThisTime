package net.blancworks.figura.avatar.components;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.blancworks.figura.avatar.FiguraNativeObject;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.terasology.jnlua.LuaState;
import org.terasology.jnlua.LuaState53;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles the lua state of the avatar.
 */
public class FiguraScriptEnvironment extends FiguraAvatarComponent<NbtCompound> {
    public LuaState53 luaState;
    public Map<String, String> trueSources;


    public FiguraScriptEnvironment(FiguraAvatar ownerAvatar) {
        super(ownerAvatar);
    }

    /**
     * Ensures the lua state has been created and has scripts loaded.
     */
    public void ensureLuaState() {
        if (luaState != null || trueSources == null || trueSources.size() == 0)
            return;

        luaState = new LuaState53();
        luaState.openLibs();

        //Track this native object to clean up later.
        ownerAvatar.trackNativeObject(new LuaEnvironmentWrapper(luaState));

        try {
            //Push "load from resource" function.
            luaState.pushJavaFunction(s -> {
                try {
                    String targetFile = s.checkString(1);
                    //Load chunk
                    luaState.load(new String(FiguraScriptEnvironment.class.getResourceAsStream(String.format("/lua/%s.lua", targetFile)).readAllBytes(), StandardCharsets.UTF_8), targetFile);
                    //Call chunk
                    luaState.call(0, 1);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return 0;
            });
            luaState.setGlobal("loadRes");

            //Load & call main avatar sandbox
            luaState.load(new String(FiguraScriptEnvironment.class.getResourceAsStream("/lua_scripts/avatar.lua").readAllBytes(), StandardCharsets.UTF_8), "figura_avatar_init");
            luaState.call(0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -- Events --

    public void tick() {
        ensureLuaState();
    }

    public void render(float deltaTime) {
        ensureLuaState();
    }

    // -- IO --
    @Override
    public void readFromNBT(NbtCompound tag) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key : tag.getKeys()) {
            builder.put(key, tag.getString(key));
        }

        trueSources = builder.build();
    }


    /**
     * Wraps the lua state so we're not holding a direct reference to the avatar
     */
    private static class LuaEnvironmentWrapper implements FiguraNativeObject {

        public LuaState state;

        public LuaEnvironmentWrapper(LuaState state){
            this.state = state;
        }

        @Override
        public void destroy() {
            state.close();
        }
    }
}
