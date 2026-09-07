# Singularity examples

Copy-pasteable consumer snippets for SingularityLib **2.0** (Paper 26.2+, JDK 25).
The GitHub wiki for this repo is not published (`Pinont/SingularityLib.wiki` 404s),
so these pages are the docs home.

| Page | What it covers |
| --- | --- |
| [CommandGroup](command-group.md) | Root command + `SubCommand` dispatch, aliases, help, registration |
| [Conversations](conversations.md) | Paper `ConversationFactory` prompts (`withModality(false)`, type `cancel` to abort) |
| [Export snippets](export-snippets.md) | Item Studio / Entity Studio Java export → `ItemCreator` / `CustomItem` |

In-game flows (Item Studio, World Creator prompts, click-to-copy) live in
[Singularity-DevTool](https://github.com/Pinont/Singularity-DevTool) on
`rework/v2` after [PR #1](https://github.com/Pinont/Singularity-DevTool/pull/1)
(`72ff53e`). See that repo’s
[`docs/examples/`](https://github.com/Pinont/Singularity-DevTool/tree/rework/v2/docs/examples)
for the menu clicks.

**API notes these examples assume:**

- Package `com.github.pinont.singularitylib` (Maven coordinates stay `io.github.pinont:singularitylib`).
- `new ItemCreator(CorePlugin.getInstance(), Material.…)` — the Plugin argument is required in 2.x.
- Consumer plugins extend `CorePlugin` and register components with `registerComponents(…)` or `@AutoRegister`.
