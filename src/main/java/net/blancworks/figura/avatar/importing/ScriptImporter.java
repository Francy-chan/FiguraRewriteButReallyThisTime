package net.blancworks.figura.avatar.importing;

import net.minecraft.nbt.NbtCompound;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ScriptImporter implements FileImporter {
    @Override
    public boolean importFiles(Path dir, NbtCompound target) {
        try {
            List<Path> scriptFiles = Files.walk(dir).filter(p -> p.toString().toLowerCase().endsWith(".lua")).collect(Collectors.toList());


            NbtCompound scriptList = new NbtCompound();
            for (Path file : scriptFiles) {
                Path relativePath = dir.relativize(file);
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
