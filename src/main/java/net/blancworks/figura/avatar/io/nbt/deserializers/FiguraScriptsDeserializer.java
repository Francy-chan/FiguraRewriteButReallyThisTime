package net.blancworks.figura.avatar.io.nbt.deserializers;

import com.google.common.collect.ImmutableMap;
import net.blancworks.figura.avatar.script.FiguraScriptEnvironment;
import net.minecraft.nbt.NbtCompound;

import java.nio.charset.StandardCharsets;

public class FiguraScriptsDeserializer implements FiguraNbtDeserializer<FiguraScriptEnvironment, NbtCompound> {

    private static FiguraScriptsDeserializer INSTANCE = new FiguraScriptsDeserializer();

    public static FiguraScriptsDeserializer getInstance() {
        return INSTANCE;
    }

    /**
     * Mostly copied from the old readFromNbt method
     * @param data
     * @return
     */
    @Override
    public FiguraScriptEnvironment deserialize(NbtCompound data) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        for (String key : data.getKeys()) {
            byte[] stringData = data.getByteArray(key);
            builder.put(key, new String(stringData, StandardCharsets.UTF_8));
        }

        return new FiguraScriptEnvironment(builder.build());
    }
}
