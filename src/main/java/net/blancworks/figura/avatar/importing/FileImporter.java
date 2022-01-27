package net.blancworks.figura.avatar.importing;

import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;

public interface FileImporter {
    /**
     * Imports files from a folder into an NBT Compound
     * @param dir The directory to search for files
     * @param target The compound to import into
     * @return True if any imports occured, false otherwise.
     */
    boolean importFiles(Path dir, NbtCompound target);


}
