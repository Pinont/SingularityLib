package com.github.pinont.singularitylib.api.manager;

import com.github.pinont.singularitylib.plugin.CorePlugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.github.pinont.singularitylib.plugin.CorePlugin.sendConsoleMessage;
import static com.github.pinont.singularitylib.plugin.CorePlugin.sendDebugMessage;

public class FileManager {

    public final JavaPlugin plugin = CorePlugin.getInstance();

    public File getFile(String fileName) {
        // Implement the logic to get the file
        return new File(plugin.getDataFolder(), fileName);
    }

    public File getFile(String subfolder, String fileName) {
        // Implement the logic to get the file
        return new File(plugin.getDataFolder() + "/" + subfolder, fileName);
    }

    public List<File> getFileFromFolder(String subfolder) {
        File folder = new File(plugin.getDataFolder() + "/" + subfolder);
        if (isFolderExists(subfolder) && Objects.requireNonNull(folder.listFiles()).length > 0) {
                return List.of(Objects.requireNonNull(folder.listFiles()));
        }
        return List.of();
    }

    public boolean isFolderExists(String subfolder) {
        File folder = new File(plugin.getDataFolder() + "/" + subfolder);
        sendDebugMessage("Checking if folder exists: " + folder.getAbsolutePath() + " - " + folder.exists());
        return folder.exists();
    }

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
