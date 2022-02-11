package net.blancworks.figura.avatar.importing.importers;

import net.blancworks.figura.FiguraMod;
import net.minecraft.nbt.NbtCompound;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptImporter implements FileImporter {

    @Override
    public List<Path> collectFiles(Path targetFolder) {
        try {
            return Files.walk(targetFolder).filter(p -> p.toString().toLowerCase().endsWith(".lua")).collect(Collectors.toList());
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean importFiles(Path rootPath, List<Path> files, NbtCompound target) {
        try {
            NbtCompound scriptList = new NbtCompound();
            for (Path file : files) {
                Path relativePath = rootPath.relativize(file);
                String fileName = relativePath.getFileName().toString().replace(".lua", "");
                String fileText = Files.readString(file);

                scriptList.putByteArray(fileName, fileText.getBytes(StandardCharsets.UTF_8));
            }

            target.put("scripts", scriptList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
