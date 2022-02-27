package net.blancworks.figura.avatar.io;

import net.blancworks.figura.avatar.AvatarMetadata;
import net.blancworks.figura.avatar.io.converters.AvatarMetadataConverter;
import net.blancworks.figura.avatar.io.nbt.serializers.FiguraAvatarSerializer;
import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Tracks all of the files in a locally-sourced avatar.
 */
public class AvatarFileSet {
    // -- Variables -- //

    public final Path rootPath;
    public final AvatarMetadata metadata;

    public AvatarFileSet(Path rootPath) {
        this.rootPath = rootPath;
        metadata = AvatarMetadataConverter.getInstance().convert(rootPath.resolve("avatar.json"));
    }

    // -- Functions -- //

    public NbtCompound getAvatarNbt() {
        return FiguraAvatarSerializer.getInstance().serialize(rootPath);
    }

}
