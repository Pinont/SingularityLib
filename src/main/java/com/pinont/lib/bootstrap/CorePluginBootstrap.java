package com.pinont.lib.bootstrap;

import com.pinont.lib.SingularityLib;
import com.pinont.lib.plugin.CorePlugin;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CorePluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        final LifecycleEventManager<@NotNull BootstrapContext> lifecycleManager = context.getLifecycleManager();

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            event.registry().register(
                    // The key of the registry
                    // Plugins should use their own namespace instead of minecraft or papermc
                    EnchantmentKeys.create(Key.key("papermc:pointy")),
                    b -> b.description(Component.text("Pointy"))
                            .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
                            .anvilCost(1)
                            .maxLevel(25)
                            .weight(10)
                            .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                            .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(3, 1))
                            .activeSlots(EquipmentSlotGroup.ANY)
            );
            Bukkit.getLogger().info("Loaded enchantment");
        }));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(PluginProviderContext context) {
        Bukkit.getLogger().info("Creating plugin");
        return new SingularityLib();
    }
}
