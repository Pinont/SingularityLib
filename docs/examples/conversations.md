# Conversations

Do not steal chat with a global `AsyncChatEvent` / `ChatEvent` listener.
DevTool replaced that hack with Paper `ConversationFactory` after
[PR #1](https://github.com/Pinont/Singularity-DevTool/pull/1) merged into
`rework/v2` at
[`72ff53e`](https://github.com/Pinont/Singularity-DevTool/commit/72ff53e941eb34636f12f54e769945abe3acebfe).

Read the merged implementation:

- [`StartConversation`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/methods/StartConversation.java)
  — per-player prompt, `withModality(false)`, escape `cancel`
- [`PromptWorldInput`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/methods/PromptWorldInput.java)
  — world name / border (positive int) / seed (long)
- [`ConfigEditorMenu`](https://github.com/Pinont/Singularity-DevTool/blob/72ff53e941eb34636f12f54e769945abe3acebfe/src/main/java/com/github/pinont/devtool/menu/submenu/ConfigEditorMenu.java)
  — string config keys use the same `StartConversation.ask(…)`

In-game clicks: DevTool
[`docs/examples/in-game.md`](https://github.com/Pinont/Singularity-DevTool/blob/rework/v2/docs/examples/in-game.md).

`ConversationFactory` is deprecated-for-removal on Paper 26.2 (Dialogs replace
it later). It is still the API DevTool and MockBukkit use on this target.

## Copy-paste: minimal consumer prompt

Drop this helper next to your `CorePlugin` subclass. It matches DevTool’s
contract: **not modal** (other chat is not captured unless this player is in
the prompt), **60s timeout**, type **`cancel`** (or time out) to abort.

```java
package com.example.arena.prompt;

import com.github.pinont.singularitylib.plugin.CorePlugin;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

@SuppressWarnings({"deprecation", "removal"})
public final class AskPlayer {

    public static final String ESCAPE = "cancel";

    private AskPlayer() {
    }

    /**
     * Closes the current inventory and begins a modal-off chat prompt.
     * Type {@code cancel} (or wait out the timeout) to abort.
     */
    public static Conversation ask(Player player, String promptText,
                                   Consumer<String> onAnswer, Runnable onCancel) {
        Plugin plugin = CorePlugin.getInstance();
        player.closeInventory();

        ConversationFactory factory = new ConversationFactory(plugin)
                .withModality(false)
                .withLocalEcho(true)
                .withTimeout(60)
                .withEscapeSequence(ESCAPE)
                .withPrefix(context -> "Arena » ")
                .thatExcludesNonPlayersWithMessage("Players only.")
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext context) {
                        return promptText;
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input == null || input.isBlank()) {
                            return this;
                        }
                        onAnswer.accept(input.trim());
                        return Prompt.END_OF_CONVERSATION;
                    }
                })
                .addConversationAbandonedListener(event -> {
                    if (!event.gracefulExit() && onCancel != null) {
                        onCancel.run();
                    }
                });

        Conversation conversation = factory.buildConversation(player);
        conversation.begin();
        return conversation;
    }
}
```

## World-name / border / seed (PromptWorldInput pattern)

Same shape as DevTool’s World Creator. Close the GUI, prompt, parse, reopen:

```java
AskPlayer.ask(player,
        "Please send a world name into chat (or type cancel).",
        name -> player.sendMessage("World name: " + name),
        () -> player.sendMessage("Cancelled."));

AskPlayer.ask(player,
        "Please send a world border size into chat (or type cancel).",
        input -> {
            try {
                int parsed = Integer.parseInt(input);
                if (parsed <= 0) {
                    player.sendMessage("World border size must be greater than 0");
                    return;
                }
                player.sendMessage("Border: " + parsed);
            } catch (NumberFormatException e) {
                player.sendMessage("World border size must be a number.");
            }
        },
        () -> player.sendMessage("Cancelled."));

AskPlayer.ask(player,
        "Please send a seed number into chat (or type cancel).",
        input -> {
            try {
                long parsed = Long.parseLong(input);
                player.sendMessage("Seed: " + parsed);
            } catch (NumberFormatException e) {
                player.sendMessage("World seed must be a number.");
            }
        },
        () -> player.sendMessage("Cancelled."));
```

Blank input re-prompts (`return this`). `cancel` fires the abandoned listener
with `gracefulExit() == false`, which runs `onCancel`. A valid answer ends the
conversation (`Prompt.END_OF_CONVERSATION`) and does **not** run `onCancel`.

## Why `withModality(false)`

`withModality(true)` blocks all other chat and commands for that player until
the prompt ends. DevTool uses **`false`** so only the conversation’s own
messages are captured; everyone else (and this player, when not prompting)
keeps a normal chat pipeline. There is no plugin-wide chat listener.
