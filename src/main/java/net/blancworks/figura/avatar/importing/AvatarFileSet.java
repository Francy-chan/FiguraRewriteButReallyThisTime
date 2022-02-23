package net.blancworks.figura.avatar.importing;

import net.blancworks.figura.avatar.importing.importers.FileImporter;
import net.blancworks.figura.avatar.newavatar.data.serializers.FiguraAvatarSerializer;
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

    public NbtCompound getAvatarNbt() {
        return FiguraAvatarSerializer.getInstance().serialize(rootPath);
    }

    public void setRefreshEvent(Consumer<AvatarFileSet> consumer){

    }

}
