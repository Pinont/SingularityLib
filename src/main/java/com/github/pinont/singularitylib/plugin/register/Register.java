package com.github.pinont.singularitylib.plugin.register;

import com.github.pinont.singularitylib.api.annotation.AutoRegister;
import com.github.pinont.singularitylib.api.command.SimpleCommand;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.manager.CommandManager;
import com.github.pinont.singularitylib.api.manager.CustomItemManager;
import com.github.pinont.singularitylib.api.utils.Console;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for automatically registering annotated components.
 * <p>
 * Primary path: reads the compile-time {@code @AutoRegister} index written by the
 * {@code singularitylib-processor} annotation processor from every
 * {@code META-INF/singularitylib/auto-register-index.properties} resource visible on
 * the runtime classpath, instantiates each listed class via its no-arg constructor
 * and sorts it into listeners, commands and custom items.
 * <p>
 * Fallback path: if no index resource exists (legacy consumers compiled without the
 * processor), {@link #scanAndCollect(String)} performs the old slow Reflections
 * classpath scan. Both paths share the same classification logic.
 */
public class Register {

    /**
     * Classpath resource path of the compile-time auto-register index. Kept in sync
     * with {@code AutoRegisterProcessor#INDEX_RESOURCE} in the
     * {@code singularitylib-processor} module.
     */
    public static final String INDEX_RESOURCE = "META-INF/singularitylib/auto-register-index.properties";

    /**
     * Guards against registering the same classes twice in a process: the library
     * ships as a standalone bootstrap plugin and consumers join its classpath, so
     * every plugin on the server can see the library's own index as well as any
     * other plugin's. The first plugin to finish loading wins; later ones skip the
     * shared classes instead of double-registering listeners/commands/items.
     */
    private static final Set<String> ALREADY_INSTANTIATED = Collections.synchronizedSet(new HashSet<>());

    /**
     * Resets the cross-process dedupe set. Used by tests between runs;
     * not intended for production use.
     */
    public static void resetDedupe() {
        ALREADY_INSTANTIATED.clear();
    }

    private final Set<Listener> listeners = new HashSet<>();
    private final List<SimpleCommand> commands = new ArrayList<>();
    private final List<CustomItem> customItems = new ArrayList<>();

    /**
     * Default constructor for Register.
     */
    public Register() {
    }

    /**
     * Loads the compile-time {@code @AutoRegister} index from every resource named
     * {@value #INDEX_RESOURCE} on the runtime classpath and collects the listed
     * classes for registration.
     * <p>
     * The classpath is resolved from {@code Thread.currentThread().getContextClassLoader()},
     * which on a Paper server is the {@code PluginClassLoader} of the plugin doing
     * the lookup. Because the library plugin declares
     * {@code join-classpath: true} in its {@code paper-plugin.yml}, every consumer
     * plugin can also see the library's own jar (and thus its index).
     *
     * @return {@code true} if at least one index was found and processed,
     *         {@code false} if no index resource exists anywhere on the classpath
     */
    public boolean loadFromIndex() {
        return loadFromIndex(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Loads the compile-time {@code @AutoRegister} index using an explicit
     * classloader.
     *
     * @param classLoader the classloader to resolve index resources with
     * @return {@code true} if at least one index was found and processed,
     *         {@code false} if no index resource exists
     */
    public boolean loadFromIndex(ClassLoader classLoader) {
        if (classLoader == null) {
            return false;
        }
        boolean found = false;
        try {
            Enumeration<URL> resources = classLoader.getResources(INDEX_RESOURCE);
            while (resources.hasMoreElements()) {
                found = true;
                URL url = resources.nextElement();
                try (InputStream in = url.openStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    collectFromIndex(reader, classLoader);
                } catch (IOException e) {
                    Console.logError("Failed to read auto-register index: " + url);
                    Console.logError(e.getMessage());
                }
            }
        } catch (IOException e) {
            Console.logError("Failed to enumerate auto-register index: " + INDEX_RESOURCE);
            Console.logError(e.getMessage());
        }
        return found;
    }

    /**
     * Parses an index stream: one fully-qualified class name per line, blank lines
     * and {@code #} comments ignored. Every class gets instantiated through its
     * no-arg constructor and classified into listeners, commands and custom items
     * (shared with the Reflections fallback path).
     *
     * @param reader      the index reader
     * @param classLoader the classloader to load listed classes with
     * @throws IOException if the stream cannot be read
     */
    private void collectFromIndex(BufferedReader reader, ClassLoader classLoader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String className = line.trim();
            if (className.isEmpty() || className.startsWith("#")) {
                continue;
            }
            try {
                Class<?> clazz = Class.forName(className, false, classLoader);
                instantiateIfApplicable(clazz);
            } catch (ClassNotFoundException | LinkageError e) {
                // The consumer jar records classes that may not be loadable in every
                // context (e.g. the library plugin's own index listing a listener
                // whose dependencies are only present inside a consumer). Log and
                // continue instead of failing the whole plugin.
                Console.logError("Could not load class from auto-register index: " + className);
                Console.logError(e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Instantiates the given class through its no-arg constructor and classifies it
     * into listeners, commands and custom items — the shared classification logic
     * used by both the index path and the Reflections fallback.
     *
     * @param clazz the class to examine
     */
    private void instantiateIfApplicable(Class<?> clazz) {
        // Skip classes that have already been registered by another plugin on the
        // shared classpath (first plugin wins; see ALREADY_INSTANTIATED).
        if (!ALREADY_INSTANTIATED.add(clazz.getName())) {
            return;
        }
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (Listener.class.isAssignableFrom(clazz)) {
                listeners.add((Listener) instance);
            }
            if (SimpleCommand.class.isAssignableFrom(clazz)) {
                commands.add((SimpleCommand) instance);
            }
            if (CustomItem.class.isAssignableFrom(clazz)) {
                customItems.add((CustomItem) instance);
            }
        } catch (NoSuchMethodException e) {
            Console.logError("No default constructor found for class: " + clazz.getName());
        } catch (InstantiationException e) {
            Console.logError("Failed to instantiate class: " + clazz.getName());
        } catch (IllegalAccessException e) {
            Console.logError("Illegal access while instantiating class: " + clazz.getName());
        } catch (Exception e) {
            Console.logError("Unexpected error while processing class: " + clazz.getName());
            Console.logError(e.getMessage());
        }
    }

    /**
     * Explicitly registers component CLASSES (no index, no classpath scanning).
     * Each class is instantiated via its no-arg constructor and classified into
     * listeners / commands / custom items — the fast, explicit path.
     *
     * @param classes the component classes to register (must have a no-arg constructor)
     */
    public void register(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            instantiateIfApplicable(clazz);
        }
    }

    /**
     * Explicitly registers component INSTANCES (already constructed by the caller) —
     * the fastest path, with no reflection at all.
     *
     * @param instances the component instances to register
     */
    public void register(Object... instances) {
        for (Object instance : instances) {
            collect(instance);
        }
    }

    /**
     * Classifies a single instance into the appropriate collection.
     */
    private void collect(Object instance) {
        if (instance instanceof Listener) {
            listeners.add((Listener) instance);
        }
        if (instance instanceof SimpleCommand) {
            commands.add((SimpleCommand) instance);
        }
        if (instance instanceof CustomItem) {
            customItems.add((CustomItem) instance);
        }
    }

    /**
     * Scans the specified package for annotated classes and collects them for registration.
     * <p>
     * <b>Deprecated:</b> the compile-time index ({@link #loadFromIndex()}) replaces
     * the Reflections classpath scan. Consumers compiled with the
     * {@code singularitylib-processor} annotation processor do not need to call this
     * method at all — {@link #loadFromIndex()} covers them. This fallback only
     * exists for legacy consumers whose jars were built without an index.
     *
     * @param packageName the package name to scan
     */
    @Deprecated
    public void scanAndCollect(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            // Nothing to scan (e.g. under MockBukkit tests where the plugin's
            // package may resolve empty). Explicitly supported no-op.
            return;
        }
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(AutoRegister.class);

        for (Class<?> clazz : annotated) {
            try {
                Object instance = clazz.getDeclaredConstructor().newInstance();
                if (Listener.class.isAssignableFrom(clazz)) {
                    listeners.add((Listener) instance);
                }
                if (SimpleCommand.class.isAssignableFrom(clazz)) {
                    commands.add((SimpleCommand) instance);
                }
                if (CustomItem.class.isAssignableFrom(clazz)) {
                    customItems.add((CustomItem) instance);
                }
            } catch (NoSuchMethodException e) {
                Console.logError("No default constructor found for class: " + clazz.getName());
            } catch (InstantiationException e) {
                Console.logError("Failed to instantiate class: " + clazz.getName());
            } catch (IllegalAccessException e) {
                Console.logError("Illegal access while instantiating class: " + clazz.getName());
            } catch (Exception e) {
                Console.logError("Unexpected error while processing class: " + clazz.getName());
                Console.logError(e.getMessage());
            }
        }
    }

    /**
     * Registers all collected components with the specified plugin.
     *
     * @param plugin the plugin to register components with
     */
    public void registerAll(Plugin plugin) {
        // Register listeners
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
        new CommandManager().register(plugin, commands);
        // Register custom items
        for (CustomItem item : customItems) {
            if (item.getItem() != null) {
                item.register();
            }
        }
        new CustomItemManager().register(customItems);
    }

    /**
     * Gets the collected listeners.
     *
     * @return set of listeners
     */
    public Set<Listener> getListeners() {
        return listeners;
    }

    /**
     * Gets the collected commands.
     *
     * @return list of commands
     */
    public List<SimpleCommand> getCommands() {
        return commands;
    }

    /**
     * Gets the collected custom items.
     *
     * @return list of custom items
     */
    public List<CustomItem> getCustomItems() {
        return customItems;
    }
}