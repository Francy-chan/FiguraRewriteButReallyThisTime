package net.blancworks.figura.importing;

import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.ArrayList;

public class ImporterManager {
    // -- Variables --
    private static ArrayList<FileImporter> allImporters = new ArrayList<>();


    // -- Functions --

    static void init(){
        allImporters.add(new BlockbenchModelImporter());
    }

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
