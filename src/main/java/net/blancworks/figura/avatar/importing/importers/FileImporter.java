package net.blancworks.figura.avatar.importing.importers;

import net.minecraft.nbt.NbtCompound;

import java.nio.file.Path;
import java.util.List;

public interface FileImporter {

    /**
     * Collects the files in a folder that should be imported by this importer.
     */
    List<Path> collectFiles(Path targetFolder);

    /**
     * Imports files from a folder into an NBT Compound
     */
    boolean importFiles(Path rootPath, List<Path> files, NbtCompound target);

}
