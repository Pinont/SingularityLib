package com.pinont.lib.plugin;

import com.pinont.lib.api.creator.items.HeadItemCreator;
import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.Layout;
import com.pinont.lib.api.ui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DevTool {
    // pattern
    /*
    ===========
    middle dirt block [ServerName, Version, plugins int]
    =============
    center stuff
    =============
    * */

    // World Creator
    // playerList [kick, de-op]

    public DevTool(Player player) {
        Menu devMenu = new Menu("Dev Tool", 9*5);
        devMenu.setLayout("=========", "====i====", "=========", "==w=p=o==", "=========");
        devMenu.setKey(
                new Layout() {
                    @Override
                    public char getKey() {
                        return '=';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).setDisplayName(" ").create();
                            }

                            @Override
                            public void onClick(Player player) {

                            }
                        };
                    }
                },
                new Layout() {

                    @Override
                    public char getKey() {
                        return 'i';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {

                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.GRASS_BLOCK)).setDisplayName(ChatColor.GREEN + "Server Info").addLore(ChatColor.GRAY + "Server: " + ChatColor.YELLOW + Bukkit.getServer().getName(), ChatColor.GRAY + "Version: " + ChatColor.YELLOW + Bukkit.getServer().getVersion(), ChatColor.GRAY + "Plugins (" + ChatColor.YELLOW + Bukkit.getServer().getPluginManager().getPlugins().length + ChatColor.GRAY + ")").create();
                            }

                            @Override
                            public void onClick(Player player) {

                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'p';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new HeadItemCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setDisplayName("Player List").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                getPlayerManager(player);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'w'; // worldcreator
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.COARSE_DIRT)).setDisplayName("Worlds").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                getWorldManager(player);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'o'; // itemCreator
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                }
        );
        devMenu.show(player);
    }

    private void getWorldManager(Player p) {
        Menu worldManagerMenu = new Menu("World Manager", 9);
        int count = 0;
        for (World world : Bukkit.getWorlds()) {
            int finalCount = count;
            worldManagerMenu.addButton(new Button() {
                @Override
                public int getSlot() {
                    return finalCount;
                }

                @Override
                public ItemStack getItem() {
                    return switch (world.getEnvironment()) {
                        case NORMAL -> new ItemCreator(new ItemStack(Material.GRASS_BLOCK)).setDisplayName(world.getName()).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
                        case NETHER -> new ItemCreator(new ItemStack(Material.NETHERRACK)).setDisplayName(world.getName()).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
                        case THE_END -> new ItemCreator(new ItemStack(Material.END_STONE)).setDisplayName(world.getName()).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
                        default -> new ItemCreator(new ItemStack(Material.GOLD_BLOCK)).setDisplayName(world.getName()).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
                    };
                }

                @Override
                public void onClick(Player player) {

                }
            });
            count++;
        }
        int finalCount = count;
        worldManagerMenu.addButton(new Button() {
            @Override
            public int getSlot() {
                return finalCount;
            }

            @Override
            public ItemStack getItem() {
                return new ItemCreator(new ItemStack(Material.BEDROCK)).setDisplayName(ChatColor.YELLOW + "Click to create new world").create();
            }

            @Override
            public void onClick(Player player) {

            }
        });
        worldManagerMenu.show(p);
    }

    private void getPlayerManager(Player p) {
//        int max = 45;
        Menu playerManager = new Menu("Player Manager", 9); // temp
        for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i++) {
            Player player = (Player) Bukkit.getOnlinePlayers().toArray()[i];
            int finalI = i;
            playerManager.addButton(new Button() {
                @Override
                public int getSlot() {
                    return finalI;
                }

                @Override
                public ItemStack getItem() {
                    return new HeadItemCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setDisplayName(player.getName()).setCannotMove(true).create();
                }

                @Override
                public void onClick(Player player) {

                }
            });
        }
        playerManager.show(p);
    }
}
