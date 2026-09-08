# Export snippets (ItemCreator / CustomItem)

DevTool Item Studio and Entity Studio emit ready-to-paste Java that reconstructs
what you built with SingularityLib APIs. Merged on DevTool `rework/v2` at
[`72ff53e`](https://github.com/Pinont/Singularity-DevTool/commit/72ff53e941eb34636f12f54e769945abe3acebfe)
([PR #1](https://github.com/Pinont/Singularity-DevTool/pull/1)).

Generators:

- [`ItemSnippet`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/methods/ItemSnippet.java)
- [`EntitySnippet`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/methods/EntitySnippet.java)
- Delivery: [`ExportSnippet`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/methods/ExportSnippet.java)
  — Adventure `ClickEvent.copyToClipboard`, chat preview, written book

In-game clicks:
[`docs/examples/in-game.md`](https://github.com/Pinont/Singularity-DevTool/blob/rework/v2/docs/examples/in-game.md)
on DevTool.

**2.x constructor:** always
`new ItemCreator(CorePlugin.getInstance(), Material.…)` (or
`(CorePlugin.getInstance(), Material.…, amount)` / `(CorePlugin.getInstance(), itemStack)`).
There is no Plugin-less `ItemCreator(Material)` anymore.

## What a generated ItemCreator snippet looks like

Picking **diamond sword** in Item Studio → **Export ItemCreator** produces
exactly this (from `ItemSnippet.studioItemCreator(Material.DIAMOND_SWORD)`):

```java
import com.github.pinont.singularitylib.api.enums.AttributeType;
import com.github.pinont.singularitylib.api.items.Attributes;
import com.github.pinont.singularitylib.api.items.ItemCreator;
import com.github.pinont.singularitylib.plugin.CorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

ItemStack item = new ItemCreator(CorePlugin.getInstance(), Material.DIAMOND_SWORD)
        .setName(Component.text("Studio: Diamond sword", NamedTextColor.LIGHT_PURPLE))
        .addLore(Component.text("Made in the DevTool Item Studio", NamedTextColor.GRAY))
        .create();
item = Attributes.setAttribute(item, AttributeType.ATTACK_DAMAGE, 5.0,
        AttributeModifier.Operation.ADD_NUMBER);
```

Paste inside a player-scoped method (or any place that already has a running
`CorePlugin`). Then give it:

```java
player.getInventory().addItem(item);
```

`Attributes.setAttribute` returns a **new** `ItemStack` (original untouched).
Keep the reassignment.

The extra `NamespacedKey` / `Enchantment` imports are always emitted by the
studio generator so held-item reverse-engineering (`ItemSnippet.fromItem`) can
add `.addEnchant(…)` lines without a second import pass. Unused imports are
safe to delete.

## What a generated CustomItem snippet looks like

**Export CustomItem** for a golden apple
(`ItemSnippet.studioCustomItem(Material.GOLDEN_APPLE)`):

```java
import com.github.pinont.singularitylib.api.enums.AttributeType;
import com.github.pinont.singularitylib.api.items.Attributes;
import com.github.pinont.singularitylib.api.items.ItemCreator;
import com.github.pinont.singularitylib.api.items.CustomItem;
import com.github.pinont.singularitylib.api.items.ItemInteraction;
import com.github.pinont.singularitylib.plugin.CorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class StudioGoldenApple extends CustomItem {

    @Override
    public ItemCreator register() {
        return new ItemCreator(CorePlugin.getInstance(), Material.GOLDEN_APPLE)
                .setName(Component.text("Studio: Golden apple", NamedTextColor.LIGHT_PURPLE))
                .addLore(Component.text("Made in the DevTool Item Studio", NamedTextColor.GRAY));
    }

    @Override
    public ItemInteraction getInteraction() {
        return null;
    }
}
```

`register()` returns the **builder**, not `create()`. `CustomItem.getItem()`
calls `register().addInteraction(getInteraction()).create()`. A `null`
interaction is skipped (`ItemCreator.addInteraction(null)` is a no-op).

### Wire it into your plugin

```java
@Override
public void onPluginStart() {
    registerComponents(new StudioGoldenApple());
}
```

Give a stack:

```java
player.getInventory().addItem(new StudioGoldenApple().getItem());
```

To make it do something, replace `return null` with an `ItemInteraction`
(right/left click set, `execute(Player)` body). Keep the class on a no-arg
constructor if you also use `@AutoRegister`.

## Held-item export (`/devtool snippet`)

`ItemSnippet.fromItem(held)` reverse-engineers amount, display name, lore,
unbreakable, and enchantments. Example for 4 stone:

```java
ItemStack item = new ItemCreator(CorePlugin.getInstance(), Material.STONE, 4)
        .create();
```

Air / empty hand exports `// No item to export.` and does not throw.

## Entity Studio spawn snippet

`/devtool entitystudio` → pick a type → **Export spawn snippet**. For a zombie
(`EntitySnippet.spawn(EntityType.ZOMBIE)`):

```java
import com.github.pinont.singularitylib.plugin.CorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

// Paste inside a player-scoped method (Player player = ...).
Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
entity.customName(Component.text("Studio: Zombie", NamedTextColor.AQUA));
entity.setCustomNameVisible(true);
// CorePlugin.getInstance() is available if you need to schedule a follow-up:
// CorePlugin.getInstance().getServer();
```

## Delivery (clipboard + book)

`ExportSnippet.toPlayer` always:

1. Closes the inventory.
2. Sends `[Click to copy snippet]` with `ClickEvent.copyToClipboard` (first
   24000 chars if the payload is huge).
3. Prints the first 12 lines in chat.
4. Gives a written book (`author: DevTool`) paginated at 240 chars. Full
   inventory drops the book at your feet.

You do not need this helper in a consumer plugin unless you are building your
own studio. Paste the Java from the book / clipboard into a `CorePlugin`
project and compile.
