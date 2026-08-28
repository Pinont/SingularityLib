package com.github.pinont.singularitylib.api.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import com.github.pinont.singularitylib.api.items.ItemCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A paginated inventory menu over a list of items of type {@code T}.
 *
 * <p>Renders {@code pageSize} entries per page into the given content slots,
 * with automatic Prev/Next buttons and a page indicator. This is the
 * foundation for every list-style view (e.g. DevTool v2's plugin lists,
 * world lists, player lists).
 *
 * @param <T> the element type
 */
public class PaginatedMenu<T> extends Menu {

    private final List<T> items;
    private final Function<T, ItemStack> itemRenderer;
    private final BiConsumer<Player, T> onClick;
    private final int pageSize;
    private final int[] contentSlots;
    private int page = 0;

    /**
     * @param plugin       the owning plugin
     * @param title        menu title
     * @param size         inventory size (multiple of 9)
     * @param pageSize     entries per page
     * @param contentSlots which slots hold content (e.g. slots 9..35)
     * @param items        the full item list
     * @param itemRenderer maps an element to its ItemStack
     * @param onClick      invoked when a content item is clicked
     */
    public PaginatedMenu(Plugin plugin, String title, int size, int pageSize,
                         int[] contentSlots, List<T> items,
                         Function<T, ItemStack> itemRenderer,
                         BiConsumer<Player, T> onClick) {
        super(plugin, title, size);
        this.items = items == null ? new ArrayList<>() : items;
        this.pageSize = pageSize;
        this.contentSlots = contentSlots == null ? defaultSlots(size) : contentSlots;
        this.itemRenderer = itemRenderer;
        this.onClick = onClick;
    }

    private static int[] defaultSlots(int size) {
        // middle region: skip top & bottom rows (0-8, last 9)
        List<Integer> s = new ArrayList<>();
        for (int i = 9; i < size - 9 && i < size; i++) {
            s.add(i);
        }
        return s.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Jumps to a specific page and re-renders the inventory for the player.
     */
    public void showPage(Player player, int targetPage) {
        int maxPage = Math.max(0, (int) Math.ceil((double) items.size() / pageSize) - 1);
        this.page = Math.max(0, Math.min(targetPage, maxPage));
        show(player);
    }

    public int getPage() {
        return page;
    }

    public int getMaxPage() {
        return Math.max(0, (int) Math.ceil((double) items.size() / pageSize) - 1);
    }

    @Override
    public void show(Player player) {
        clearButtons();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, items.size());

        // Render content slice
        List<Button> buttons = new ArrayList<>();
        for (int i = start; i < end; i++) {
            int slot = contentSlots[i - start];
            if (slot >= getSize()) break;
            T item = items.get(i);
            ItemStack stack = itemRenderer.apply(item);
            buttons.add(new Button() {
                @Override
                public int getSlot() {
                    return slot;
                }

                @Override
                public ItemStack getItem() {
                    return stack;
                }

                @Override
                public void onClick(Player p) {
                    if (onClick != null) onClick.accept(p, item);
                }
            });
        }

        // Prev / Next
        if (page > 0) {
            buttons.add(prevButton());
        }
        if (page < getMaxPage()) {
            buttons.add(nextButton());
        }

        // Page indicator
        if (getMaxPage() > 0) {
            buttons.add(pageIndicator());
        }

        buttons.forEach(this::addButton);
        super.show(player);
    }

    private Button prevButton() {
        return new Button() {
            @Override
            public int getSlot() {
                return getSize() - 9; // bottom-left
            }

            @Override
            public ItemStack getItem() {
                return prevStack();
            }

            @Override
            public void onClick(Player player) {
                showPage(player, page - 1);
            }
        };
    }

    private Button nextButton() {
        return new Button() {
            @Override
            public int getSlot() {
                return getSize() - 1; // bottom-right
            }

            @Override
            public ItemStack getItem() {
                return nextStack();
            }

            @Override
            public void onClick(Player player) {
                showPage(player, page + 1);
            }
        };
    }

    private Button pageIndicator() {
        return new Button() {
            @Override
            public int getSlot() {
                return getSize() - 5;
            }

            @Override
            public ItemStack getItem() {
                return pageIndicatorStack();
            }

            @Override
            public void onClick(Player player) {
                // no-op
            }
        };
    }

    private ItemStack prevStack() {
        return new ItemCreator(getPlugin(), Material.ARROW)
                .setName(Component.text("« Prev", NamedTextColor.YELLOW))
                .addLore(Component.text("Page " + (page) + "/" + (getMaxPage() + 1), NamedTextColor.GRAY))
                .create();
    }

    private ItemStack nextStack() {
        return new ItemCreator(getPlugin(), Material.ARROW)
                .setName(Component.text("Next »", NamedTextColor.YELLOW))
                .addLore(Component.text("Page " + (page + 2) + "/" + (getMaxPage() + 1), NamedTextColor.GRAY))
                .create();
    }

    private ItemStack pageIndicatorStack() {
        return new ItemCreator(getPlugin(), Material.PAPER)
                .setName(Component.text("Page " + (page + 1) + " / " + (getMaxPage() + 1), NamedTextColor.AQUA))
                .create();
    }
}