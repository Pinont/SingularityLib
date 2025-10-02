package com.github.pinont.singularitylib.api.manager;

import com.github.pinont.singularitylib.plugin.CorePlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

/**
 * Manages file operations for the plugin.
 * Provides utilities for file and folder management within the plugin's data directory.
 */
public class FileManager {

    /**
     * The plugin instance for accessing the data folder.
     */
    public final JavaPlugin plugin = CorePlugin.getInstance();

    /**
     * Default constructor for FileManager.
     */
    public FileManager() {
    }

    /**
     * Gets a file from the plugin's data folder.
     *
     * @param fileName the name of the file to get
     * @return the File object
     */
    public File getFile(String fileName) {
        // Implement the logic to get the file
        return new File(plugin.getDataFolder(), fileName);
    }

    /**
     * Gets a file from a specific subfolder within the plugin's data folder.
     *
     * @param subfolder the subfolder path
     * @param fileName the name of the file to get
     * @return the File object
     */
    public File getFile(String subfolder, String fileName) {
        // Implement the logic to get the file
        return new File(plugin.getDataFolder() + "/" + subfolder, fileName);
    }

    /**
     * Gets all files from a specific subfolder.
     *
     * @param subfolder the subfolder to get files from
     * @return a list of files in the subfolder, or empty list if folder doesn't exist or is empty
     */
    public List<File> getFileFromFolder(String subfolder) {
        File folder = new File(plugin.getDataFolder() + "/" + subfolder);
        if (isFolderExists(subfolder) && Objects.requireNonNull(folder.listFiles()).length > 0) {
                return List.of(Objects.requireNonNull(folder.listFiles()));
        }
        return List.of();
    }

    /**
     * Checks if a subfolder exists within the plugin's data directory.
     *
     * @param subfolder the subfolder path to check
     * @return true if the folder exists, false otherwise
     */
    public boolean isFolderExists(String subfolder) {
        File folder = new File(plugin.getDataFolder() + "/" + subfolder);
        sendDebugMessage("Checking if folder exists: " + folder.getAbsolutePath() + " - " + folder.exists());
        return folder.exists();
    }

    /**
     * Saves a file to a specific subfolder within the plugin's data directory.
     *
     * @param subfolder the subfolder to save the file to
     * @param file the file to save
     */
    public void save(String subfolder, File file) {
        if (!file.exists()) {
            sendConsoleMessage("File does not exist: " + file.getAbsolutePath());
            return;
        }
        File folder = new File(plugin.getDataFolder() + "/" + subfolder);
        if (!isFolderExists(subfolder)) {
            folder.mkdirs();
        }
        file = new File(folder, file.getName());
        try {
            file.createNewFile();
        } catch (Exception e) {
            sendConsoleMessage("Error creating file: " + e.getMessage());
        }
    }
}
