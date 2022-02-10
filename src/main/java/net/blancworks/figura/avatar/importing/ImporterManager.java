package net.blancworks.figura.avatar.importing;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.List;

public class ImporterManager {
    // -- Variables --
    private static final List<FileImporter> allImporters = new ImmutableList.Builder<FileImporter>().add(
            new BlockbenchModelImporter(),
            new ScriptImporter()
    ).build();


    // -- Functions --

    /**
     * Attempts to import an avatar from the files in a given directory.
     * @param directory The directory to check for files.
     * @param output The NBT Compound to import files to.
     * @return True if an avatar was imported, false otherwise.
     */
    public static boolean importDirectory(Path directory, NbtCompound output){
        boolean r = false;

        for (FileImporter importer : allImporters) {
            r |= importer.importFiles(directory, output);
        }

        return r;
    }
}
