package com.pinont.lib.plugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public abstract class CorePlugin extends JavaPlugin implements PluginBootstrap {

    @Getter
    private static JavaPlugin plugin;

    private static CorePlugin instance;

    public static CorePlugin getInstance() {
        if (instance == null) {
            try {
                instance = JavaPlugin.getPlugin(CorePlugin.class);

            } catch (final IllegalStateException ex) {
                if (Bukkit.getPluginManager().getPlugin("PlugManX") != null)
                    Bukkit.getLogger().severe("Failed to get instance of the plugin, if you reloaded using PlugManX you need to do a clean restart instead.");
                throw ex;
            }
            Objects.requireNonNull(instance, "Cannot get a new instance! Have you reloaded?");
        }

        return instance;
    }

    @Override
    public final void onEnable() {
        plugin = this;
        onPluginStart();
        Bukkit.getLogger().info(this.getName() + " has been enabled!");
    }

    @Override
    public void bootstrap(BootstrapContext context) { // WIP ref: https://docs.papermc.io/paper/dev/registries
//        // Register a new handler for the freeze lifecycle event on the enchantment registry
//        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
//            event.registry().register(
//                    // The key of the registry
//                    // Plugins should use their own namespace instead of minecraft or papermc
//                    EnchantmentKeys.create(Key.key("papermc:pointy")),
//                    b -> b.description(Component.text("Pointy"))
//                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
//                            .anvilCost(1)
//                            .maxLevel(25)
//                            .weight(10)
//                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
//                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
//                            .activeSlots(EquipmentSlotGroup.ANY)
//            );
//        }));
    }

    @Override
    public final void onDisable() {
        onPluginStop();
        getLogger().info(this.getName() + " has been disabled!");
    }

    public abstract void onPluginStart();

    public abstract void onPluginStop();
}
