package net.blancworks.figura.avatar.io;

import net.blancworks.figura.avatar.AvatarMetadata;
import net.blancworks.figura.avatar.io.converters.AvatarMetadataConverter;
import net.blancworks.figura.avatar.io.nbt.serializers.FiguraAvatarSerializer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Tracks all of the files in a locally-sourced avatar.
 */
public class AvatarFileSet {
    // -- Variables -- //

    public final Path rootPath;
    public final AvatarMetadata metadata;

    public AvatarFileSet(Path rootPath) {
        this.rootPath = rootPath;
        if (!rootPath.toString().endsWith(".moon"))
            metadata = AvatarMetadataConverter.getInstance().convert(rootPath.resolve("avatar.json"));
        else
            metadata = new AvatarMetadata(rootPath.getFileName().toString().replace(".moon", ""), "", "");
    }

    // -- Functions -- //

    public NbtCompound getAvatarNbt() {
        try {
            if (rootPath.toString().endsWith(".moon"))
                return NbtIo.readCompressed(rootPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FiguraAvatarSerializer.getInstance().serialize(rootPath);
    }

}
