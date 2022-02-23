package net.blancworks.figura.avatar.newavatar.data.serializers;

import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FiguraScriptsSerializer implements FiguraNbtSerializer<List<Path>, NbtCompound> {

    private Path rootPath;

    public FiguraScriptsSerializer(Path rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public NbtCompound serialize(List<Path> paths) {
        NbtCompound result = new NbtCompound();
        try {
            for (Path path : paths) {
                String name = rootPath.relativize(path).getFileName().toString().replace(".lua", "");
                String text = Files.readString(path);
                result.putByteArray(name, text.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
