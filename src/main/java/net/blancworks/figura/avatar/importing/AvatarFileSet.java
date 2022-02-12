package net.blancworks.figura.avatar.importing;

import net.blancworks.figura.avatar.importing.importers.FileImporter;
import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tracks all of the files in a locally-sourced avatar.
 */
public class AvatarFileSet {
    // -- Variables -- //

    public Path rootPath;
    //Stores each fill importer and the files they want to import.
    public HashMap<FileImporter, List<Path>> discoveredFiles = new HashMap<>();

    private Consumer<AvatarFileSet> consumer;


    // -- Functions -- //

    /**
     * Imports all the files from this file set into the NBT.
     */
    public void writeAvatarNBT(NbtCompound target){
        for (Map.Entry<FileImporter, List<Path>> entry : discoveredFiles.entrySet())
            entry.getKey().importFiles(rootPath, entry.getValue(), target);
    }


    public void setRefreshEvent(Consumer<AvatarFileSet> consumer){

    }

}
