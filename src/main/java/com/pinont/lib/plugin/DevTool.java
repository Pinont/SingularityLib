package com.pinont.lib.plugin;

import com.pinont.lib.api.creator.items.ItemHeadCreator;
import com.pinont.lib.api.creator.items.ItemCreator;
import com.pinont.lib.api.ui.Button;
import com.pinont.lib.api.ui.Layout;
import com.pinont.lib.api.ui.Menu;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DevTool implements SimpleCommand {

    String version = CorePlugin.getInstance().getDescription().getVersion();

    public void openDevTool(Player player) {
        Menu devMenu = new Menu(ChatColor.DARK_RED + "Developer Tools " + ChatColor.GRAY + "(" + version + ")", 9*5);
        devMenu.setLayout("=========", "====i====", "=========", "==w=p=t==", "=========");
        devMenu.setKey(
                blank(),
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
                                return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setDisplayName("Player List").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                showServerPlayerManager(player);
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
                                showServerWorldManger(player);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 't'; // tools
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.STICK).setDisplayName("Tools").addLore("More Tools").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                showOtherTools(player);
                            }
                        };
                    }
                }
        );
        devMenu.show(player);
    }

    private void showOtherTools(Player player) {
        new Menu("Heldable Tool").setLayout("=========", "==m=w=o==", "=========").setKey(
                blank(),
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'm'; // mobCreator
                    }

                    @Override
                    public Button getButton() {
                        return null;
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
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'w'; // later
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                }
        ).show(player);

    }

    private ItemStack getWorldEnvironmentBlock(World world) {
        return switch (world.getEnvironment()) {
            case NORMAL -> new ItemStack(Material.GRASS_BLOCK);
            case NETHER -> new ItemStack(Material.NETHERRACK);
            case THE_END -> new ItemStack(Material.END_STONE);
            default -> new ItemStack(Material.COMMAND_BLOCK);
        };
    }

    private void showServerWorldManger(Player p) {
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
                    return new ItemCreator(getWorldEnvironmentBlock(world)).setDisplayName(properWorldName(world)).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
                }

                @Override
                public void onClick(Player player) {
                    showSingleWorldManager(world, player);
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

    private void showSingleWorldManager(World world, Player player) {
        Menu worldManagerMenu = new Menu("World Manager", 9 * 5);
        worldManagerMenu.setLayout("=========", "====w====", "=========", "==t===r==", "=========");
        worldManagerMenu.setKey(
                blank(),
                new Layout() {

                    @Override
                    public char getKey() {
                        return 'w';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() { // world info

                            @Override
                            public ItemStack getItem() {

                                return new ItemCreator(getWorldEnvironmentBlock(world)).setDisplayName(ChatColor.GREEN + "World Info").addLore(ChatColor.GRAY + "Name: " + ChatColor.YELLOW + properWorldName(world), ChatColor.GRAY + "Difficulty: " + ChatColor.YELLOW + world.getDifficulty(), ChatColor.GRAY + "Environment Type: " + ChatColor.YELLOW + world.getEnvironment()).create();
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
                        return 't'; // teleport
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.BEACON)).setDisplayName("Teleport").addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to Teleport").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                if (player.getWorld() != world)
                                    player.teleport(world.getSpawnLocation());
                                else {
                                    player.sendMessage(ChatColor.RED + "You are already in this world!");
                                }
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'r'; // gamerules
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                }
        ).show(player);
    }

    private String properWorldName(World world) {
        String formattedName = world.getName().replace("_", " ");
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : formattedName.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == ' ') {
                capitalizeNext = true;
            }
        }

        return result.toString();
    }

    private Layout blank() {
        return new Layout() {
            @Override
            public char getKey() {
                return '=';
            }

            @Override
            public Button getButton() {
                return new Button() {
                    @Override
                    public ItemStack getItem() {
//                        return new ItemCreator(new ItemStack(Material.WHITE_STAINED_GLASS_PANE)).setDisplayName(" ").create();
                        return new ItemStack(Material.AIR);
                    }

                    @Override
                    public void onClick(Player player) {

                    }
                };
            }
        };
    }

    private void showServerPlayerManager(Player origin) {
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
                    return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setDisplayName(player.getName()).create();
                }

                @Override
                public void onClick(Player player) {
                    showSpecificPlayerManager(origin, player);
                }
            });
        }
        playerManager.show(origin);
    }

    private void showSpecificPlayerManager(Player origin, Player target) {
        Menu playerManager = new Menu("Player Manager", 9 * 5);
        playerManager.setLayout("====p====", "=========", "==t=i=o==", "==b=k=n==");
        playerManager.setKey(
                blank(),
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
                                String firstPlayedDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                                        .format(new java.util.Date(target.getFirstPlayed()));
                                return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD))
                                        .setOwner(target.getName())
                                        .setDisplayName(target.getName())
                                        .addLore(ChatColor.BOLD + "" + ChatColor.GRAY + "First Joined: " + ChatColor.YELLOW + firstPlayedDate)
                                        .create();
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
                        return 't'; // teleport
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.BEACON)).setDisplayName("Teleport").addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to Teleport").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.teleport(target.getLocation());
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'i'; // player Inventory
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'b';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.ANVIL).setDisplayName(ChatColor.RED + "Ban").addLore(ChatColor.RED + "Click to ban.").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                showBanPlayerApproval(player, target);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'k';
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.REDSTONE).setDisplayName(ChatColor.RED + "Kick").addLore(ChatColor.RED + "Click to kick.").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                showKickPlayerApproval(player, target);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'o';
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'n';
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                }
        ).show(origin);
    }

    private void showBanPlayerApproval(Player origin, Player target) {
        new Menu(ChatColor.RED + "Are you sure to ban " + target.getName() + "?", 9 * 5)
                .setLayout("=========", "====p====", "=========", "==a===d==", "=========")
                .setKey(blank(),
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
                                        return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(target.getName()).setDisplayName(ChatColor.RED + "Are you sure to ban " + target.getName() + "?").create();
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
                                return 'a';
                            }

                            @Override
                            public Button getButton() {
                                return new Button() {
                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(Material.GREEN_STAINED_GLASS).setDisplayName(ChatColor.GREEN + "ACCEPT").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        target.ban("You have been banned from this server.", (Date) null, player.getName(), true);
                                    }
                                };
                            }
                        },
                        new Layout() {
                            @Override
                            public char getKey() {
                                return 'd';
                            }

                            @Override
                            public Button getButton() {
                                return new Button() {
                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(Material.RED_STAINED_GLASS).setDisplayName(ChatColor.RED + "DENY").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        showSpecificPlayerManager(player, target);
                                    }
                                };
                            }
                        }
                ).show(origin);
    }

    private void showKickPlayerApproval(Player origin, Player target) {
        new Menu(ChatColor.RED + "Are you sure to kick " + target.getName() + "?", 9 * 5)
                .setLayout("=========", "====p====", "=========", "==a===d==", "=========")
                .setKey(blank(),
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
                                        return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(target.getName()).setDisplayName(ChatColor.RED + "Are you sure to kick " + target.getName() + "?").create();
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
                                return 'a';
                            }

                            @Override
                            public Button getButton() {
                                return new Button() {
                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(Material.GREEN_STAINED_GLASS).setDisplayName(ChatColor.GREEN + "ACCEPT").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        target.kick();
                                    }
                                };
                            }
                        },
                        new Layout() {
                            @Override
                            public char getKey() {
                                return 'd';
                            }

                            @Override
                            public Button getButton() {
                                return new Button() {
                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(Material.RED_STAINED_GLASS).setDisplayName(ChatColor.RED + "DENY").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        showSpecificPlayerManager(player, target);
                                    }
                                };
                            }
                        }
                ).show(origin);
    }

    /// Commands

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            this.openDevTool(player);
            return;
        }
        commandSourceStack.getSender().sendMessage("This command can only be executed by a player!");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        return switch (args.length) {
//            case 0: {
//                yield Collections.singletonList("tool");
//            }
            default: {
                yield Collections.emptyList();
            }
        };
    }


    @Override
    public boolean canUse(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return sender.hasPermission("pinont.devtool");
        } else {
            return false;
        }
    }

    @Override
    public String getName() {
        return "devTool";
    }

    @Override
    public String description() {
        return "SingularityLib Developer Tools";
    }
}
