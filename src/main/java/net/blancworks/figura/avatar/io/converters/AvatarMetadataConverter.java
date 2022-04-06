package net.blancworks.figura.avatar.io.converters;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.blancworks.figura.avatar.AvatarMetadata;

import java.nio.file.Files;
import java.nio.file.Path;

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
            String author = getString(metadata, "author", "");
            String cardColor = getString(metadata, "color", "");
            String background = getString(metadata, "background", "");

            return new AvatarMetadata(avatarName, author, cardColor, background);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getString(JsonObject targetObject, String key, String def){
        return targetObject.has(key) ? targetObject.get(key).getAsString() : def;
    }
}
