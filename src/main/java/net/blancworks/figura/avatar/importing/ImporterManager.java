package net.blancworks.figura.avatar.importing;

import com.google.common.collect.ImmutableList;
import net.blancworks.figura.FiguraMod;
import net.blancworks.figura.avatar.importing.importers.BlockbenchModelImporter;
import net.blancworks.figura.avatar.importing.importers.FileImporter;
import net.blancworks.figura.avatar.importing.importers.ScriptImporter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ImporterManager {
    // -- Variables --
    public static final List<FileImporter> allImporters = new ImmutableList.Builder<FileImporter>().add(
            new BlockbenchModelImporter(),
            new ScriptImporter()
    ).build();

    private static int importTimer = 20;

    public static HashMap<Path, AvatarFileSet> foundAvatars = new HashMap<>();
    private static HashSet<Path> notFoundPaths = new HashSet<>();
    private static Path rootFolder;


    // -- Functions --

    public static void init() {
        ClientTickEvents.START_CLIENT_TICK.register(c -> tick());
        rootFolder = FiguraMod.getLocalAvatarDirectory();
    }

    public static void tick() {
        //Run once every 20 ticks.
        if (importTimer > 0) {
            importTimer--;
            return;
        }
        importTimer = 20;
        updateFoundAvatars();
    }

    public static void updateFoundAvatars(){
        notFoundPaths.clear();
        notFoundPaths.addAll(foundAvatars.keySet());

        try {
            Files.walk(rootFolder, 1).filter((p) -> !p.equals(rootFolder) && Files.isDirectory(p)).forEach(ImporterManager::importFolder);
        } catch (Exception e) {
            FiguraMod.LOGGER.error(e);
        }

        //Remove avatars we didn't find.
        for (Path notFoundPath : notFoundPaths) {
            AvatarFileSet set = foundAvatars.remove(notFoundPath);

            if (set != null) {
                //TODO - Add delete event
                FiguraMod.LOGGER.info("Figura avatar at " + notFoundPath + " wasn't found after refresh, deleting.");
            }
        }
    }

    //Searches the folder for an avatar metadata file. If none is found, it attempts the same on sub-folders.
    private static void importFolder(Path folderPath) {
        Path metaFile = folderPath.resolve("avatar.json");

        //Import avatar from folder.
        if (Files.exists(metaFile)) {
            importAvatarFromFolder(folderPath);
        } else {//Check sub-folders.
            try {
                Files.walk(folderPath, 1).filter((p) -> !p.equals(folderPath) && Files.isDirectory(p)).forEach(ImporterManager::importFolder);
            } catch (Exception e) {
                FiguraMod.LOGGER.error(e);
            }
        }
    }

    private static void importAvatarFromFolder(Path folderPath) {
        Path relative = rootFolder.relativize(folderPath);
        notFoundPaths.remove(relative);

        foundAvatars.computeIfAbsent(relative, ImporterManager::constructFileSet);
    }

    /**
     * Finds and lists all the files in a folder for an avatar.
     */
    private static AvatarFileSet constructFileSet(Path path) {
        FiguraMod.LOGGER.info("Importing avatar from folder " + path);
        AvatarFileSet set = new AvatarFileSet();
        set.rootPath = rootFolder.resolve(path);

        return set;
    }

}
