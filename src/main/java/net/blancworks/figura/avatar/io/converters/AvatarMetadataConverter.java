package net.blancworks.figura.avatar.io.converters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.avatar.AvatarMetadata;
import net.blancworks.figura.avatar.io.nbt.serializers.FiguraModelMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AvatarMetadataConverter implements FiguraConverter<Path, AvatarMetadata> {

    private static final AvatarMetadataConverter INSTANCE = new AvatarMetadataConverter();
    public static AvatarMetadataConverter getInstance() {
        return INSTANCE;
    }
    private AvatarMetadataConverter() {}



    @Override
    public AvatarMetadata convert(Path data) {
        try {
            JsonObject metadata = JsonParser.parseString(Files.readString(data)).getAsJsonObject();

            String avatarFolderName = data.getParent().getFileName().toString();
            String avatarName = getString(metadata, "name", avatarFolderName);
            String cardBack = getString(metadata, "card_back", "default");

            return new AvatarMetadata(avatarName, cardBack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getString(JsonObject targetObject, String key, String def){
        return targetObject.has(key) ? targetObject.get(key).getAsString() : def;
    }
}
