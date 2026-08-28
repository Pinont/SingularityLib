package com.github.pinont.singularitylib.api.manager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A comment-preserving, auto-updating configuration manager (Phase 2 upgrade).
 *
 * <p>Unlike {@link ConfigManager} (plain YamlConfiguration), this class:
 * <ul>
 *   <li>Preserves inline comments and section headers when saving.</li>
 *   <li>Merges missing defaults from a bundled resource WITHOUT destroying user edits.</li>
 *   <li>Exposes a {@link #mergeDefaults(Map)} API and auto-merges on first load.</li>
 * </ul>
 *
 * <p>Comment model: a {@code path -> comment} map. When a key is saved the comment is
 * emitted above it. Keys without comments render plain.
 */
public class CommentConfigManager {

    private final File configFile;
    private final YamlConfiguration config;
    private final Map<String, String> pathComments = new LinkedHashMap<>();

    /**
     * @param plugin   owning plugin
     * @param fileName config file name under the plugin data folder
     */
    public CommentConfigManager(Plugin plugin, String fileName) {
        this.configFile = new File(plugin.getDataFolder(), fileName);
        config = new YamlConfiguration();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("Could not create config: " + fileName + " -> " + e.getMessage());
            }
        }
        try {
            config.load(configFile);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Could not load config: " + fileName + " -> " + e.getMessage());
        }
    }

    /**
     * Attaches a comment to a path (emitted above that key on save).
     *
     * @param path    the dotted config path
     * @param comment the comment text (without '#')
     * @return this manager for chaining
     */
    public CommentConfigManager comment(String path, String comment) {
        pathComments.put(path, comment);
        return this;
    }

    /**
     * Sets a value and saves (preserving comments).
     */
    public void set(String path, Object value) {
        config.set(path, value);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    /**
     * Merges missing defaults from the given map into the config (existing user values win).
     * Returns true if anything was added and a save is advised.
     */
    public boolean mergeDefaults(Map<String, Object> defaults) {
        boolean changed = false;
        for (Map.Entry<String, Object> e : defaults.entrySet()) {
            if (!config.contains(e.getKey())) {
                config.set(e.getKey(), e.getValue());
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Saves to disk with comments preserved.
     */
    public void save() {
        try {
            String rendered = renderWithComments();
            Files.write(configFile.toPath(), rendered.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            Bukkit.getLogger().warning("Could not save config: " + configFile.getName() + " -> " + e.getMessage());
        }
    }

    /**
     * Renders the config as YAML with comments inserted above their paths.
     * Uses a simple dotted-path walk; nested sections get indented properly.
     */
    private String renderWithComments() {
        StringBuilder out = new StringBuilder();
        writeSection(out, "", config.getValues(false), 0);
        return out.toString();
    }

    private void writeSection(StringBuilder out, String prefix, Map<String, Object> values, int indent) {
        String pad = " ".repeat(indent);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String path = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            String comment = pathComments.get(path);
            if (comment != null) {
                for (String line : comment.split("\n")) {
                    out.append(pad).append("# ").append(line).append('\n');
                }
            }
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) nested;
                out.append(pad).append(entry.getKey()).append(":\n");
                writeSection(out, path, map, indent + 2);
            } else if (value instanceof ConfigurationSection section) {
                out.append(pad).append(entry.getKey()).append(":\n");
                writeSection(out, path, section.getValues(false), indent + 2);
            } else {
                out.append(pad).append(entry.getKey()).append(": ").append(renderScalar(value)).append('\n');
            }
        }
    }

    private String renderScalar(Object value) {
        if (value instanceof String s) {
            // Quote strings that YAML would mis-parse
            return needsQuoting(s) ? '"' + s.replace("\"", "\\\"") + '"' : s;
        }
        return String.valueOf(value);
    }

    private boolean needsQuoting(String s) {
        return s.isEmpty() || s.matches(".*[:#\\[\\]{}&*!|>'\"%@`].*") || s.matches("^(true|false|null|yes|no|\\d+.*)$");
    }

    /**
     * @return the underlying Bukkit configuration (read-only access recommended)
     */
    public YamlConfiguration getConfig() {
        return config;
    }
}