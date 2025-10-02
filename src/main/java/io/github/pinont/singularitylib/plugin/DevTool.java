package io.github.pinont.singularitylib.plugin;

import io.github.pinont.singularitylib.api.command.SimpleCommand;
import io.github.pinont.singularitylib.api.items.CustomItem;
import io.github.pinont.singularitylib.api.items.ItemCreator;
import io.github.pinont.singularitylib.api.items.ItemHeadCreator;
import io.github.pinont.singularitylib.api.items.ItemInteraction;
import io.github.pinont.singularitylib.api.manager.WorldManager;
import io.github.pinont.singularitylib.api.ui.Button;
import io.github.pinont.singularitylib.api.ui.Layout;
import io.github.pinont.singularitylib.api.ui.Menu;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static io.github.pinont.singularitylib.plugin.CorePlugin.getAPIVersion;
import static io.github.pinont.singularitylib.plugin.CorePlugin.getInstance;

/**
 * Developer tool for testing and debugging plugin functionality.
 * Provides a comprehensive interface for developers to test various features
 * including world management, teleportation, and other debugging utilities.
 *
 * @deprecated since 2.1.0, will be removed in future versions
 */
@Deprecated(since = "2.1.0", forRemoval = true)
public class DevTool extends CustomItem implements SimpleCommand, Listener {

    private final String version = getAPIVersion();

    /**
     * Default constructor for DevTool.
     */
    public DevTool() {
    }

    @Override
    public ItemCreator register() {
        return new ItemCreator(Material.DIAMOND).setName(ChatColor.DARK_RED + "Developer Tool").setUnstackable(true).addInteraction(
                new ItemInteraction() {
                    @Override
                    public String getName() {
                        return "DevTool";
                    }

                    @Override
                    public Set<Action> getAction() {
                        return Set.of(Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK);
                    }

                    @Override
                    public void execute(Player player) {
                        openDevTool(player);
                    }
                }
        );
    }

    /**
     * Opens the developer tool interface for the specified player.
     *
     * @param player the player to open the interface for
     */
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
                                return new ItemCreator(new ItemStack(Material.GRASS_BLOCK)).setName(ChatColor.GREEN + "Server Info").addLore(ChatColor.GRAY + "Server: " + ChatColor.YELLOW + Bukkit.getServer().getName(), ChatColor.GRAY + "Version: " + ChatColor.YELLOW + Bukkit.getServer().getVersion(), ChatColor.GRAY + "Plugins (" + ChatColor.YELLOW + Bukkit.getServer().getPluginManager().getPlugins().length + ChatColor.GRAY + ")").create();
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
                                return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setName("Player List").create();
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
                                return new ItemCreator(new ItemStack(Material.COARSE_DIRT)).setName("Worlds").create();
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
                                return new ItemCreator(Material.STICK).setName("Tools").addLore("More Tools").create();
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
        new Menu("Heldable Tool", 9 * 3).setLayout("=========", "==m=w=o==", "=========").setKey(
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
        return getWorldEnvironmentBlock(world.getEnvironment());
    }

    private ItemStack getWorldEnvironmentBlock(World.Environment worldEnv) {
        return switch (worldEnv) {
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
                    return new ItemCreator(getWorldEnvironmentBlock(world)).setName(properWorldName(world)).addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to edit").create();
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
                return new ItemCreator(new ItemStack(Material.BEDROCK)).setName(ChatColor.YELLOW + "Click to create new world").create();
            }

            @Override
            public void onClick(Player player) {
                showWorldCreator(player);
            }
        });
        worldManagerMenu.show(p);
    }

    private void showWorldCreator(Player player) {
        getWorldCreatorMenu(null, World.Environment.NORMAL, WorldType.NORMAL, true, 1000, Difficulty.EASY, new Random().nextLong(System.currentTimeMillis())).show(player);
    }

    /**
     * Shows the world creator interface with specified parameters.
     *
     * @param player the player to show the interface to
     * @param world_name the name of the world to create
     * @param environment the world environment type
     * @param worldType the world type
     * @param generate_structure whether to generate structures
     * @param borderSize the world border size
     * @param difficulty the world difficulty
     * @param seed the world seed
     */
    public void showWorldCreator(Player player, String world_name, World.Environment environment, WorldType worldType, boolean generate_structure, int borderSize, Difficulty difficulty, long seed) {
        getWorldCreatorMenu(world_name, environment, worldType, generate_structure, borderSize, difficulty, seed).show(player);
    }

    /**
     * Interface for capturing world creator content and user input.
     * Used to maintain state during world creation process when players input values via chat.
     */
    public interface WorldCreatorContent {
        /**
         * Gets the type of input content being processed.
         *
         * @return the input content type
         */
        String getInputContent();

        /**
         * Gets the name of the world being created.
         *
         * @return the world name
         */
        String getWorldName();

        /**
         * Gets the world environment type.
         *
         * @return the world environment
         */
        World.Environment getEnvironment();

        /**
         * Gets the world type for generation.
         *
         * @return the world type
         */
        WorldType getWorldType();

        /**
         * Gets whether structures should be generated in the world.
         *
         * @return true if structures should be generated, false otherwise
         */
        boolean getGenerateStructure();

        /**
         * Gets the world border size.
         *
         * @return the border size in blocks
         */
        int getBorderSize();

        /**
         * Gets the world difficulty setting.
         *
         * @return the difficulty level
         */
        Difficulty getDifficulty();

        /**
         * Gets the world seed for generation.
         *
         * @return the world seed
         */
        Long getSeed();
    }

    private Menu getWorldCreatorMenu(String name, World.Environment environment, WorldType worldType, boolean generate_structure, int borderSize, Difficulty difficulty, Long seed) {
        Menu worldCreatorMenu = new Menu("World Creator").setLayout("----w----", "-=n=e=t=-", "-=g=b=s=-", "----c----");
        return worldCreatorMenu.setKey(
                blank(),
                new Layout() {
                    @Override
                    public char getKey() {
                        return '-';
                    } // border

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.YELLOW_STAINED_GLASS_PANE).setName(" ").create();
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
                        return 'w';
                    } // world creator icon

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(new ItemStack(Material.GOLD_BLOCK)).setName(ChatColor.BOLD + "" + ChatColor.YELLOW + "World Creator").addLore(
                                        ChatColor.GRAY + "Name: " + ChatColor.YELLOW + (name == null ? ChatColor.RED + "Not Set" : name),
                                        ChatColor.GRAY + "Environment Type: " + ChatColor.YELLOW + (environment == null ? ChatColor.RED + "Not Set" : environment),
                                        ChatColor.GRAY + "World Type: " + ChatColor.YELLOW + (worldType == null ? ChatColor.RED + "Not Set" : worldType),
                                        ChatColor.GRAY + "Difficulty: " + ChatColor.YELLOW + difficulty,
                                        ChatColor.GRAY + "Generate Structure: " + ChatColor.YELLOW + (generate_structure ? "True" : "False"),
                                        ChatColor.GRAY + "Border Size: " + ChatColor.YELLOW + borderSize,
                                        ChatColor.GRAY + "Seed: " + ChatColor.YELLOW + (seed == null ? ChatColor.RED + "RANDOM" : seed)
                                ).create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.closeInventory();
                                player.sendMessage(ChatColor.YELLOW + "World '" + name + "' is Creating...");
                                createWorld(name, environment, worldType, generate_structure, borderSize, difficulty, seed);
                                player.sendMessage(ChatColor.GREEN + "World '" + name + "' has been created!");
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'n';
                    } // set world name

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                if (name == null) {
                                    return new ItemCreator(Material.OAK_SIGN).setName("Set World Name").addLore(ChatColor.YELLOW + "Click to set world name.").create();
                                }
                                return new ItemCreator(Material.BIRCH_SIGN).setName(name).addLore(ChatColor.YELLOW + "Click to change world name.").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.sendMessage(ChatColor.GRAY + "Please send a world name into chat.");
                                player.setMetadata("devTool", new FixedMetadataValue(getInstance(), new WorldCreatorContent() {
                                    @Override
                                    public String getInputContent() {
                                        return "worldName";
                                    }

                                    @Override
                                    public String getWorldName() {
                                        return name;
                                    }

                                    @Override
                                    public World.Environment getEnvironment() {
                                        return environment;
                                    }

                                    @Override
                                    public WorldType getWorldType() {
                                        return worldType;
                                    }

                                    @Override
                                    public boolean getGenerateStructure() {
                                        return generate_structure;
                                    }

                                    @Override
                                    public int getBorderSize() {
                                        return borderSize;
                                    }

                                    @Override
                                    public Difficulty getDifficulty() {
                                        return difficulty;
                                    }

                                    @Override
                                    public Long getSeed() {
                                        return seed;
                                    }
                                }));
                                player.closeInventory();
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'e'; // environment
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                if (environment == null) {
                                    return new ItemCreator(Material.COMMAND_BLOCK).setName("Set World Environment").addLore(ChatColor.YELLOW + "Click to change world environment.").create();
                                }
                                return new ItemCreator(getWorldEnvironmentBlock(environment)).setName(environment.name()).addLore(ChatColor.YELLOW + "Click to change world environment.").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                World.Environment[] environments = World.Environment.values();
                                int currentIndex = environment == null ? -1 : java.util.Arrays.asList(environments).indexOf(environment);
                                int nextIndex = (currentIndex + 1) % environments.length;
                                showWorldCreator(player, name, environments[nextIndex], worldType, generate_structure, borderSize, difficulty, seed);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 't'; // world type
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                if (worldType == null) {
                                    return new ItemCreator(Material.OAK_SAPLING).setName("Set World Type").addLore(ChatColor.YELLOW + "Click to change world type.").create();
                                }
                                return new ItemCreator(Material.CHERRY_SAPLING).setName(worldType.getName()).addLore(ChatColor.YELLOW + "Click to change world type").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                WorldType[] worldTypes = WorldType.values();
                                int currentTypeIndex = worldType == null ? -1 : java.util.Arrays.asList(worldTypes).indexOf(worldType);
                                int nextTypeIndex = (currentTypeIndex + 1) % worldTypes.length;

                                showWorldCreator(player, name, environment, worldTypes[nextTypeIndex], generate_structure, borderSize, difficulty, seed);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'g'; // generate structure? def = true
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                if (generate_structure) return new ItemCreator(Material.BIRCH_STAIRS).setName(ChatColor.GRAY + "Generate Structure: " + ChatColor.GREEN + "True").create();
                                return new ItemCreator(Material.ACACIA_STAIRS).setName(ChatColor.GRAY + "Generate Structure: " + ChatColor.GREEN + "False").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                showWorldCreator(player, name, environment, worldType, !generate_structure, borderSize, difficulty, seed);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'b'; // border size? def = default
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.STRUCTURE_VOID).setName(ChatColor.GRAY + "World Border Size: " + ChatColor.YELLOW + borderSize).create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.sendMessage(ChatColor.GRAY + "Please send a world border size into chat.");
                                player.setMetadata("devTool", new FixedMetadataValue(getInstance(), new WorldCreatorContent() {
                                    @Override
                                    public String getInputContent() {
                                        return "worldBorder";
                                    }

                                    @Override
                                    public String getWorldName() {
                                        return name;
                                    }

                                    @Override
                                    public World.Environment getEnvironment() {
                                        return environment;
                                    }

                                    @Override
                                    public WorldType getWorldType() {
                                        return worldType;
                                    }

                                    @Override
                                    public boolean getGenerateStructure() {
                                        return generate_structure;
                                    }

                                    @Override
                                    public int getBorderSize() {
                                        return borderSize;
                                    }

                                    @Override
                                    public Difficulty getDifficulty() {
                                        return difficulty;
                                    }

                                    @Override
                                    public Long getSeed() {
                                        return seed;
                                    }
                                }));
                                player.closeInventory();
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 's'; // seed? def = default
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.WHEAT_SEEDS).setName(ChatColor.GRAY + "Seed: " + ChatColor.YELLOW + seed).create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.sendMessage(ChatColor.GRAY + "Please send a seed number into chat.");
                                player.setMetadata("devTool", new FixedMetadataValue(getInstance(), new WorldCreatorContent() {
                                    @Override
                                    public String getInputContent() {
                                        return "worldSeed";
                                    }

                                    @Override
                                    public String getWorldName() {
                                        return name;
                                    }

                                    @Override
                                    public World.Environment getEnvironment() {
                                        return environment;
                                    }

                                    @Override
                                    public WorldType getWorldType() {
                                        return worldType;
                                    }

                                    @Override
                                    public boolean getGenerateStructure() {
                                        return generate_structure;
                                    }

                                    @Override
                                    public int getBorderSize() {
                                        return borderSize;
                                    }

                                    @Override
                                    public Difficulty getDifficulty() {
                                        return difficulty;
                                    }

                                    @Override
                                    public Long getSeed() {
                                        return seed;
                                    }
                                }));
                                player.closeInventory();
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'c'; // create button
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.DIAMOND).setName(ChatColor.WHITE + "Create World").addLore(ChatColor.YELLOW + "Click to create world.").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.closeInventory();
                                player.sendMessage(ChatColor.YELLOW + "World '" + name + "' is Creating...");
                                createWorld(name, environment, worldType, generate_structure, borderSize, difficulty, seed);
                                player.sendMessage(ChatColor.GREEN + "World '" + name + "' has been created!");
                            }
                        };
                    }
                }
        );
    }

    private void createWorld(String name, World.Environment environment, WorldType worldType, boolean generate_structure, int borderSize, Difficulty difficulty, Long seed) {
        if (name == null) {
            name = "custom_world_" + environment.name() + "_" + worldType.getName() + "_" + System.currentTimeMillis();
        }
        new WorldManager(name).create(worldType, environment, generate_structure, borderSize, difficulty, seed);
    }

    private void showSingleWorldManager(World world, Player player) {
        Menu worldManagerMenu = new Menu(world.getName() + ": World Manager");
        worldManagerMenu.setLayout("=========", "====w====", "=========", "==t=d=r==", "=========");
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

                                return new ItemCreator(getWorldEnvironmentBlock(world)).setName(ChatColor.GREEN + "World Info").addLore(ChatColor.GRAY + "Name: " + ChatColor.YELLOW + properWorldName(world), ChatColor.GRAY + "Difficulty: " + ChatColor.YELLOW + world.getDifficulty(), ChatColor.GRAY + "Environment Type: " + ChatColor.YELLOW + world.getEnvironment()).create();
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
                                return new ItemCreator(new ItemStack(Material.BEACON)).setName("Teleport").addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to Teleport").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                if (player.getWorld() != world) {
                                    player.sendMessage(ChatColor.GRAY + "Teleporting to " + properWorldName(world) + "...");
                                    player.teleport(world.getSpawnLocation());
                                } else {
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
                },
                worldDeleteButton(world)
        ).show(player);
    }

    private Layout worldDeleteButton(World world) {
        if (world.hasMetadata("loader")) {
            return new Layout() {
                @Override
                public char getKey() {
                    return 'd';
                }

                @Override
                public Button getButton() {
                    return new Button() {
                        @Override
                        public ItemStack getItem() {
                            return new ItemCreator(Material.RED_STAINED_GLASS).setName(ChatColor.RED +"Delete").addLore(ChatColor.RED + "Click here to delete this world").create();
                        }

                        @Override
                        public void onClick(Player player) {
                            showDeleteWorldApproval(player, world);
                        }
                    };
                }
            };
        }
        return new Layout() {
            @Override
            public char getKey() {
                return 'd';
            }

            @Override
            public Button getButton() {
                return new Button() {
                    @Override
                    public ItemStack getItem() {
                        return new ItemCreator(Material.AIR).create();
                    }

                    @Override
                    public void onClick(Player player) {

                    }
                };
            }
        };
    }

    private void showDeleteWorldApproval(Player player, World targetWorld) {
        new Menu(ChatColor.RED + "Are you sure to delete " + targetWorld.getName() + "?")
                .setLayout("=========", "====w====", "=========", "==a===d==", "=========")
                .setKey(blank(),
                        new Layout() {
                            @Override
                            public char getKey() {
                                return 'w';
                            }

                            @Override
                            public Button getButton() {
                                return new Button() {
                                    @Override
                                    public ItemStack getItem() {
                                        return new ItemCreator(new ItemStack(getWorldEnvironmentBlock(targetWorld))).setName(ChatColor.RED + "Are you sure to delete " + targetWorld.getName() + "?").create();
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
                                        return new ItemCreator(Material.GREEN_STAINED_GLASS).setName(ChatColor.GREEN + "ACCEPT").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        WorldManager.delete(targetWorld.getName());
                                        player.sendMessage(ChatColor.RED + targetWorld.getName() + " is now mark for removal!");
                                        showServerWorldManger(player);
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
                                        return new ItemCreator(Material.RED_STAINED_GLASS).setName(ChatColor.RED + "DENY").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        showSingleWorldManager(targetWorld, player);
                                    }
                                };
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
                    return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(player.getName()).setName(player.getName()).create();
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
        playerManager.setLayout("====p====", "=========", "==t=i=o==", "==b=k=n==", "====v====", "=========");
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
                                        .setName(target.getName())
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
                                return new ItemCreator(new ItemStack(Material.BEACON)).setName("Teleport").addLore(ChatColor.BOLD + "" + ChatColor.YELLOW + "Click to Teleport").create();
                            }

                            @Override
                            public void onClick(Player player) {
                                player.teleport(target.getLocation());
                                player.closeInventory();
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
                                return new ItemCreator(Material.ANVIL).setName(ChatColor.RED + "Ban").addLore(ChatColor.RED + "Click to ban.").create();
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
                                return new ItemCreator(Material.REDSTONE).setName(ChatColor.RED + "Kick").addLore(ChatColor.RED + "Click to kick.").create();
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
                        return 'o'; // op Player
                    }

                    @Override
                    public Button getButton() {
                        return null;
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() {
                        return 'n'; // invincibility
                    }

                    @Override
                    public Button getButton() {
                        return new Button() {
                            @Override
                            public ItemStack getItem() {
                                return new ItemCreator(Material.TOTEM_OF_UNDYING).setName("God: " + target.isInvulnerable()).create();
                            }

                            @Override
                            public void onClick(Player player) {
                                target.setInvulnerable(!target.isInvulnerable());
                                if (Bukkit.getServer().getAllowFlight()) {
                                    target.setAllowFlight(target.isInvulnerable());
                                }
                                else
                                    player.sendMessage(ChatColor.RED + "You need to enable flight to use flying feature.");
                                showSpecificPlayerManager(player, target);
                            }
                        };
                    }
                },
                new Layout() {
                    @Override
                    public char getKey() { // vanish
                        return 'v';
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
                                        return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(target.getName()).setName(ChatColor.RED + "Are you sure to ban " + target.getName() + "?").create();
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
                                        return new ItemCreator(Material.GREEN_STAINED_GLASS).setName(ChatColor.GREEN + "ACCEPT").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        target.ban("You have been banned from this server.", (Date) null, player.getName(), true);
                                        showServerPlayerManager(origin);
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
                                        return new ItemCreator(Material.RED_STAINED_GLASS).setName(ChatColor.RED + "DENY").create();
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
                                        return new ItemHeadCreator(new ItemStack(Material.PLAYER_HEAD)).setOwner(target.getName()).setName(ChatColor.RED + "Are you sure to kick " + target.getName() + "?").create();
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
                                        return new ItemCreator(Material.GREEN_STAINED_GLASS).setName(ChatColor.GREEN + "ACCEPT").create();
                                    }

                                    @Override
                                    public void onClick(Player player) {
                                        target.kick();
                                        showServerPlayerManager(origin);
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
                                        return new ItemCreator(Material.RED_STAINED_GLASS).setName(ChatColor.RED + "DENY").create();
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

    /**
     * Handles player chat events for world creator input.
     * Processes user input for world creation parameters when players are in world creator mode.
     *
     * @param event the player chat event
     */
    @EventHandler
    public void sendChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.hasMetadata("devTool")) {
            DevTool.WorldCreatorContent worldCreatorContent = (DevTool.WorldCreatorContent) player.getMetadata("devTool").getFirst().value();
            if (worldCreatorContent.getInputContent() == null) {
                player.sendMessage(ChatColor.RED + "Seem like devTool World creator has occur an error.");
                return;
            }
            event.setCancelled(true);
            switch (worldCreatorContent.getInputContent()) {
                case "worldName": {
                    new DevTool().showWorldCreator(player, event.getMessage(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    break;
                }
                case "worldBorder": {
                    try {
                        int borderSize = Integer.parseInt(event.getMessage());
                        if (borderSize <= 0) {
                            player.sendMessage(ChatColor.RED + "World border size must be greater than 0");
                            new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                            return;
                        }
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), borderSize, worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "World border size must be a number.");
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    }
                    break;
                }
                case "worldSeed": {
                    try {
                        long seed = Long.parseLong(event.getMessage());
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), seed);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "World border size must be a number.");
                        new DevTool().showWorldCreator(player, worldCreatorContent.getWorldName(), worldCreatorContent.getEnvironment(), worldCreatorContent.getWorldType(), worldCreatorContent.getGenerateStructure(), worldCreatorContent.getBorderSize(), worldCreatorContent.getDifficulty(), worldCreatorContent.getSeed());
                    }
                    break;
                }
            }
            if (player.hasMetadata("devTool")) {
                player.removeMetadata("devTool", getInstance());
            }
        }
    }

    /// Commands

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] strings) {
        if (commandSourceStack.getSender() instanceof Player player) {
            switch (strings.length) {
                case 0: {
                    this.openDevTool(player);
                    break;
                }
                case 1: {
                    ItemStack devToolItem = this.getItem();
                    if (strings[0].equalsIgnoreCase("get") || strings[0].equalsIgnoreCase("getItem")) {
                        player.getInventory().addItem(devToolItem);
                    }
                    break;
                }
                case 3: {
                    if (strings[0].equalsIgnoreCase("world")) {
                        if (strings[1].equalsIgnoreCase("teleport")) {
                            World world = Bukkit.getWorld(strings[2]);
                            if (world != null) {
                                player.teleport(world.getSpawnLocation());
                                player.sendMessage(ChatColor.GRAY + "Teleporting to " + world.getName() + "...");
                            } else {
                                player.sendMessage(ChatColor.RED + "World not found!");
                            }
                        }
                        break;
                    }
                }
            }
            return;
        }
        commandSourceStack.getSender().sendMessage("This command can only be executed by a player!");
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        switch (args.length) {
            case 0, 1: {
                return List.of("get", "getItem", "world");
            }
            case 2: {
                if (args[0].equalsIgnoreCase("world")) {
                    return List.of("teleport");
                }
                break;
            }
            case 3: {
                if (args[0].equalsIgnoreCase("world") && args[1].equalsIgnoreCase("teleport")) {
                    return List.of(Bukkit.getWorlds().stream().map(World::getName).toArray(String[]::new));
                }
                break;
            }
        }
        return List.of();
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
    public ItemInteraction getInteraction() {
        return null;
    }

    @Override
    public String description() {
        return "SingularityLib Developer Tools";
    }
}
