# AGENTS.md — SingularityLib

## What this is
SingularityLib (`com.github.pinont:singularitylib`) is the core framework of the **Singularity Project** — a Minecraft plugin development library for PaperMC. Sibling repos:

| Repo | Role |
|---|---|
| **SingularityLib** (this repo) | Core API library: commands, config, GUI, items, entities, auto-registration, MySQL, Discord JDA bootstrap |
| `SingularityPlugin` | Starter template plugin consuming SingularityLib |
| `Singularity-DevTool` | In-game debugging/admin tool built on SingularityLib |

Published via **JitPack** (`com.github.pinont:SingularityLib:<tag>`) and GitHub Packages.

## Build & test
```bash
mvn clean package          # build shaded jar into target/
mvn test                   # run tests (JUnit; see src/test/java/...)
```
- Requires **JDK 23** (`<java.version>23</java.version>`).
- Targets **Paper API 1.21.8-R0.1-SNAPSHOT** (`paperapi.version` property).
- Shaded deps include `org.reflections` (used by the auto-register scanner).

## Code layout
Root package: `com.github.pinont.singularitylib`

- `api/Plugin.java` — Bukkit plugin entry point (declared in `src/main/resources/plugin.yml`, `load: STARTUP`)
- `api/annotation/AutoRegister.java` — marks classes for automatic registration
- `api/command/SimpleCommand.java` — simplified command abstraction
- `api/entity/EntityCreator.java` — entity configuration/storage helpers
- `api/enums/` — `AttributeType`, `PersisDataType` *(sic — typo of PersistentDataType)*, `PlayerInventorySlotType`
- `api/event/` — custom events: `ItemExecuteEvent`, `PlayerDamageByPlayerEvent`
- `api/hook/discordJDA/` — Discord bootstrap: `DiscordApp`, `SimpleSlashCommands`, `SlashCommandComponent`, `Ready`
- `api/items/` — `CustomItem`, `ItemCreator`, `ItemHeadCreator`, `CrossbowCreator`, `ItemInteraction`
- `api/manager/` — `CommandManager`, `ConfigManager`, `CustomItemManager`, `FileManager`, `WorldManager`
- `api/runnable/` — `Runner`, `Scheduler` task wrappers
- `api/ui/` — GUI toolkit: `Menu`, `Button`, `Layout`
- `api/utils/` — `Common`, `Console`, `MySQL`
- `plugin/CorePlugin.java` — **the contract**: consumer main classes extend this instead of `JavaPlugin`; overrides are `onPluginStart()` / `onPluginStop()`. Owns static `instance`, prefix, Folia detection, registration wiring.
- `plugin/listener/` — internal listeners (`EntityDamageListener`, `PlayerListener`) backing item interaction & PvP events
- `plugin/register/Register.java` — scans packages with Reflections for `@AutoRegister`, instantiates and registers `SimpleCommand`, `CustomItem`, `Listener` impls

Tests live in `src/test/java` (`CorePluginTest`, `CommonTest`, `ItemCreatorMetaTest`, `TestPlugin`). Coverage is minimal — don't assume green tests mean full safety.

## Conventions
- Plain Java (no Kotlin anywhere in this repo).
- Public API classes get full Javadoc — keep that up when editing them (javadoc jar is built via maven-javadoc-plugin).
- Version bumps happen through release tags; `-SNAPSHOT` between releases.

## Known issues / tech debt (observed)
1. **Version confusion**: `pom.xml` says `1.3.4-SNAPSHOT`, tags go to `1.3.3`, but `RELEASE_NOTES.md` describes a "2.2.0" initial release. Align before any re-release.
2. **Legacy color API**: `org.bukkit.ChatColor` used across `Console`, `Register`, etc. — deprecated in modern Paper (use Adventure `Component`/`NamedTextColor`).
3. **Typo'd enum** `PersisDataType` — public API surface, renaming is breaking.
4. **Reflections scanner** (`Register`) is startup-slow and fragile under complex classpaths/relocation; a compile-time index or explicit registry would be more robust.
5. **Ships as a plugin AND a library**: `plugin.yml` makes the lib itself a plugin (`main: ...api.Plugin`). Consumers instead depend on it as a Maven dep and extend `CorePlugin`. This dual role is a design smell worth revisiting during the rework.
6. **JitPack coordinates mismatch**: consumers use `com.github.pinont:SingularityLib` (capital L) while this pom declares `com.github.pinont:singularitylib`.
7. Minimal tests; no CI matrix across MC versions.

## Agent guidance
- **Do not break `CorePlugin`'s public contract** (`onPluginStart`/`onPluginStop`, `getConfigManager`, static accessors) — both sibling repos and unknown external plugins depend on it.
- Treat all classes under `api/` as public API; check JitPack consumers before renaming/moving anything.
- New features go in the matching `api/` subpackage with Javadoc; wire global behavior through `CorePlugin`/`Register`, not static singletons where avoidable.
- When touching items/events, remember the internal listeners in `plugin/listener` are the machinery behind the public events.
- The owner plans a **major rework**: LTS/rolling MC version support, wiki + javadoc site. Favor changes that reduce coupling to a single Paper version (e.g., isolate NMS/version-specific code) over quick hacks.
