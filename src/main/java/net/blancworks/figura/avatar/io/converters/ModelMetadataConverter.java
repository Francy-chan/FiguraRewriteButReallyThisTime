package net.blancworks.figura.avatar.io.converters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.avatar.io.nbt.serializers.FiguraModelMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ModelMetadataConverter implements FiguraConverter<Path, FiguraModelMetadata> {

    private static final ModelMetadataConverter INSTANCE = new ModelMetadataConverter();
    public static ModelMetadataConverter getInstance() {
        return INSTANCE;
    }
    private ModelMetadataConverter() {}

    @Override
    public FiguraModelMetadata convert(Path data) {
        try {
            JsonObject metadata = JsonParser.parseString(Files.readString(data)).getAsJsonObject();
            Map<String, JsonObject> properties = new HashMap<>();
            if (metadata.has("modelMetadata")) {
                JsonObject modelMetadata = metadata.getAsJsonObject("modelMetadata");
                for (Map.Entry<String, JsonElement> entry : modelMetadata.entrySet()) {
                    if (entry.getValue() instanceof JsonObject obj) {
                        properties.put(entry.getKey(), obj);
                    } else {
                        throw new IOException("Invalid properties type: must be object!");
                    }
                }
            }
            return new FiguraModelMetadata(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
