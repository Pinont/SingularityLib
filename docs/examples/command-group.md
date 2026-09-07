# CommandGroup

`CommandGroup` is a `SimpleCommand` that owns a map of `SubCommand`s. With no
arguments it prints a gold/yellow help listing. With a first argument it
dispatches to the matching subcommand and passes **the remaining args**.

Verified against
[`CommandGroup.java`](https://github.com/Pinont/SingularityLib/blob/main/src/main/java/com/github/pinont/singularitylib/api/command/CommandGroup.java)
and
[`SubCommand.java`](https://github.com/Pinont/SingularityLib/blob/main/src/main/java/com/github/pinont/singularitylib/api/command/SubCommand.java)
on **`main`**. Production usage:
[`DevToolCommand`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/commands/DevToolCommand.java)
on DevTool `rework/v2` (`72ff53e`).

## Register a group

`CommandGroup` is a `SimpleCommand`, so it registers the same way as any other
command. Prefer explicit registration from `onPluginStart()`:

```java
package com.example.arena;

import com.github.pinont.singularitylib.plugin.CorePlugin;

public class ArenaPlugin extends CorePlugin {

    @Override
    public void onPluginStart() {
        registerComponents(new ArenaCommand());
    }

    @Override
    public void onPluginStop() {
    }
}
```

`getName()` may list aliases with colons. `CommandManager` splits on `:` and
registers each token. `"arena:ar"` becomes `/arena` and `/ar`.

You can also mark the group `@AutoRegister` (no-arg constructor required) if
your build runs `singularitylib-processor`. DevTool itself uses
`registerComponents(new DevToolCommand())` instead of a scan.

## Copy-paste: root + two subcommands

```java
package com.example.arena;

import com.github.pinont.singularitylib.api.command.CommandGroup;
import com.github.pinont.singularitylib.api.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand extends CommandGroup {

    public ArenaCommand() {
        registerSubcommand(new CreateSub());
        registerSubcommand(new DeleteSub());
        registerSubcommand(new ListSub());
    }

    @Override
    public String getName() {
        return "arena:ar";
    }
}

final class CreateSub extends SubCommand {

    @Override
    public String getName() {
        return "create:c";
    }

    @Override
    public String getDescription() {
        return "Create an arena";
    }

    @Override
    public String getPermission() {
        return "arena.create";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /arena create <name>", NamedTextColor.YELLOW));
            return;
        }
        player.sendMessage(Component.text("Created arena " + args[0], NamedTextColor.GREEN));
    }
}

final class DeleteSub extends SubCommand {

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "Delete an arena";
    }

    @Override
    public String getPermission() {
        return "arena.delete";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /arena delete <name>", NamedTextColor.YELLOW));
            return;
        }
        sender.sendMessage(Component.text("Deleted arena " + args[0], NamedTextColor.RED));
    }
}

final class ListSub extends SubCommand {

    @Override
    public String getName() {
        return "list:ls";
    }

    @Override
    public String getDescription() {
        return "List arenas";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("Arenas: (none yet)", NamedTextColor.GRAY));
    }
}
```

## Behaviour to expect

| Input | Result |
| --- | --- |
| `/arena` | Auto help: `—— arena:ar help ——` then one yellow line per subcommand (`getName()` is printed as-is, including aliases) |
| `/arena create spawn` | `CreateSub.execute` with `args = ["spawn"]` (subcommand name stripped) |
| `/arena c spawn` | Same — `create:c` registers both `create` and `c` |
| `/arena nope` | Red `Unknown subcommand: nope. Use /arena:ar help` |
| Console `/arena create spawn` | Red `This subcommand is players-only.` (`isPlayerOnly()`) |
| No permission | Red `You do not have permission to use: /arena:ar create:c` |

Empty `getPermission()` / `null` means no permission check. Empty
`getDescription()` omits the ` — …` suffix on the help line.

The sender-based `execute(CommandSender, String[])` path is what tests should
call (see `CommandGroupTest` in this repo). Paper still enters through
`execute(CommandSourceStack, String[])`, which forwards to the sender overload.

## Override the root (DevTool pattern)

If no-args should **not** print help, override `execute(CommandSender, String[])`
and only call `super.execute` for known subcommands. DevTool does this so
`/devtool` opens a GUI and `/devtool itemstudio` still dispatches:

```java
@Override
public void execute(CommandSender sender, String[] args) {
    if (args.length == 0) {
        sender.sendMessage(Component.text("Open the menu, or /arena help", NamedTextColor.YELLOW));
        return;
    }
    if (getSubcommandNames().contains(args[0].toLowerCase())) {
        super.execute(sender, args);
        return;
    }
    sender.sendMessage(Component.text("Unknown: " + args[0], NamedTextColor.RED));
}
```

`getSubcommandNames()` returns every registered key (primary names **and**
aliases), in insertion order.

## What CommandGroup does not do

- It does not implement Brigadier/tab suggestions. `SimpleCommand` extends
  Paper `BasicCommand`; add `suggest(CommandSourceStack, String[])` yourself if
  you want subcommand completion.
- `paper-plugin.yml` has no `commands:` block in the bootstrap model. Registration
  is programmatic via `CommandManager` / `LifecycleEvents.COMMANDS`.
- Subcommand classes are **not** `SimpleCommand`s. Do not `@AutoRegister` a
  `SubCommand` by itself — register it on a `CommandGroup`.
