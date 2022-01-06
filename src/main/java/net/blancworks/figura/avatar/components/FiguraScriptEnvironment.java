package net.blancworks.figura.avatar.components;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.FiguraAvatar;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.terasology.jnlua.LuaState53;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Handles the lua state of the avatar.
 */
public class FiguraScriptEnvironment extends FiguraAvatarComponent {
    public LuaState53 luaState;
    public Map<String, String> trueSources;


    public FiguraScriptEnvironment(FiguraAvatar ownerAvatar) {
        super(ownerAvatar);
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

    @Override
    public void writeToNBT(NbtCompound tag) {
        //Put all scripts into NBT, and into name list
        for (Map.Entry<String, String> entry : trueSources.entrySet()) {
            byte[] stringData = entry.getValue().getBytes(StandardCharsets.UTF_8);
            tag.putByteArray(entry.getKey(), stringData);
        }
    }
}
