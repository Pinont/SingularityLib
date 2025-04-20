package com.pinont.lib;

import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.Interaction;
import com.pinont.lib.api.ui.Menu;
import com.pinont.lib.enums.AttributeType;
import com.pinont.lib.plugin.CorePlugin;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public final class SingularityLib extends CorePlugin implements Listener {

    @Override
    public void onPluginStart() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onPluginStop() {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getInventory().addItem(new ItemCreator(new ItemStack(Material.DIAMOND_SWORD))
                .addAttribute(AttributeType.ATTACK_DAMAGE, 100, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND)
                .addInteraction(new Interaction() {
            @Override
            public String getName() {
                return "Give OP";
            }

            @Override
            public Set<Action> getAction() {
                return Set.of(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
            }

            @Override
            public void execute(Player player) {
                new Menu("Test Menu")
                        .addButton(new Button() {
                                    @Override
                                    public int getSlot() {
                                        return 0;
                                    }

                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(new ItemStack(Material.DIAMOND_SWORD)).create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        player.closeInventory();
                                        player.damage(1);
                                    }
                                }
                        ).displayTo(player);
            }
        }).create());
    }

}
