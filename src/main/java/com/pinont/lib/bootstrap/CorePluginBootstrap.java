package com.pinont.lib.bootstrap;

import com.pinont.lib.api.creator.EnchantmentCreator;
import com.pinont.lib.plugin.CorePlugin;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.pinont.lib.plugin.CorePlugin.sendConsoleMessage;

public class CorePluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(BootstrapContext context) {

        final LifecycleEventManager<@NotNull BootstrapContext> lifecycleManager = context.getLifecycleManager();

        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            for (EnchantmentCreator.EnchantmentBlueprint blueprint : EnchantmentCreator.getEnchantments()) {
                sendConsoleMessage("Loading custom enchantment " + blueprint.getNamespace() + "." + blueprint.getName());
                event.registry().register(
                        EnchantmentKeys.create(Key.key(blueprint.getNamespace() + "." + blueprint.getName())),
                        b -> b.description(Component.text(blueprint.getDescription()))
                                .supportedItems(event.getOrCreateTag(blueprint.getSupportItem()))
                                .anvilCost(blueprint.getAnvilCost())
                                .maxLevel(blueprint.getMaxLevel())
                                .weight(blueprint.getFoundWeight())
                                .minimumCost(blueprint.getMinimumCost())
                                .maximumCost(blueprint.getMaximumCost())
                                .activeSlots(blueprint.getActiveSlotGroup())
                );
            }
        }));
    }

    @Override
    public @NotNull JavaPlugin createPlugin(PluginProviderContext context) {
        return new CorePlugin() {
            @Override
            public void onPluginStart() {}

            @Override
            public void onPluginStop() {}
        };
    }
}
