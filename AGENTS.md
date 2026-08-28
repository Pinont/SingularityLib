# AGENTS.md — SingularityLib

## What this is
SingularityLib (`io.github.pinont:singularitylib`) is the core framework of the **Singularity Project** — a Minecraft plugin development library for PaperMC. Sibling repos:

| Repo | Role |
|---|---|
| **SingularityLib** (this repo) | Core API library: commands, config, GUI, items, entities, auto-registration, MySQL, Discord JDA bootstrap |
| `SingularityPlugin` | Starter template plugin consuming SingularityLib |
| `Singularity-DevTool` | In-game debugging/admin tool built on SingularityLib |

Published to **GitHub Packages** only (`https://maven.pkg.github.com/pinont/SingularityLib`). JitPack is no longer part of the build.

## Branches
- Modernization work lives on **`rework/v2`** (v2 platform/toolchain pass: Paper 26.2+, JDK 25, Folia flag, GitHub Packages). Never push directly to `main`; merge via PR when a pass is complete.

## Build & test
```bash
export JAVA_HOME=$(echo ~/dev-tools/jdk-*)
~/dev-tools/apache-maven-3.9.16/bin/mvn clean package   # jar + sources + javadoc into target/
~/dev-tools/apache-maven-3.9.16/bin/mvn test            # JUnit + MockBukkit
```
- Requires **JDK 25** (`<java.version>25</java.version>`).
- Targets **Paper API 26.2+ latest-only**: `paperapi.version` is the range `[26.2.build,)`, which resolves to the newest build on repo.papermc.io at build time. The standalone `folia-api` dep stays pinned at its last line `26.2.build.7-beta` (from Paper 26.x the Folia schedulers ship inside paper-api).
- Tests run against **MockBukkit `mockbukkit-v26.2`** (the old `mockbukkit-v1.21` artifact cannot load 26.x APIs).
- Shaded deps include `org.reflections` (used by the deprecated auto-register fallback only), behind the `-Dshade` profile. `org.reflections` is marked `<optional>true</optional>` so it never leaks into consumers.
- The `singularitylib-processor` submodule (own pom, coordinates `io.github.pinont:singularitylib-processor`, Java 25, zero deps) is a reactor member of the root pom: it generates `META-INF/singularitylib/auto-register-index.properties` during compilation. The root pom references it via `annotationProcessorPaths` and a `provided` dependency so the lib's own `@AutoRegister` classes (e.g. `EntityDamageListener`) are indexed too.

## Code layout
Root package: `io.github.pinont.singularitylib`

- `api/Plugin.java` — Bukkit plugin entry point (declared in `src/main/resources/paper-plugin.yml`, `load: STARTUP`)
- `api/annotation/AutoRegister.java` — marks classes for automatic registration
- `api/command/SimpleCommand.java` — simplified command abstraction
- `api/entity/EntityCreator.java` — entity configuration/storage helpers
- `api/enums/` — `AttributeType`, `PersisDataType` *(sic — typo of PersistentDataType)*, `PlayerInventorySlotType`
- `api/event/` — custom events: `ItemExecuteEvent`, `PlayerDamageByPlayerEvent`
- `api/hook/discordJDA/` — Discord bootstrap: `DiscordApp`, `SimpleSlashCommands`, `SlashCommandComponent`, `Ready`
- `api/items/` — `CustomItem`, `ItemCreator`, `ItemHeadCreator`, `CrossbowCreator`, `ItemInteraction`
- `api/manager/` — `CommandManager`, `ConfigManager`, `CustomItemManager`, `FileManager`, `WorldManager`
- `api/runnable/` — `Runner`, `Scheduler` task wrappers. **Tick semantics:** delay/period are server ticks on both Paper and Folia paths (Folia `runAtFixedRate` and Bukkit `runTaskTimer` are tick-native); the only exception is `runRepeatingTaskAsync`, whose delay/period follow the supplied `TimeUnit`. Folia region-scheduler tasks have no bulk cancel API — cancel them via task handles/`cancelTask()`; `cancelAllTasks()` covers async + global-region (Folia) or everything (Paper).
- `api/ui/` — GUI toolkit: `Menu`, `Button`, `Layout`
- `api/utils/` — `Common`, `Console`, `MySQL`
- `plugin/CorePlugin.java` — **the contract**: consumer main classes extend this instead of `JavaPlugin`; overrides are `onPluginStart()` / `onPluginStop()`. Owns static `instance`, prefix, Folia detection, registration wiring. The lib itself is a bootstrap plugin declared in `src/main/resources/paper-plugin.yml` (`api-version: '26.2'`, `folia-supported: true`, `load: STARTUP`).
- `plugin/listener/` — internal listeners (`EntityDamageListener`, `PlayerListener`) backing item interaction & PvP events
- `plugin/register/Register.java` — auto-registration coordinator. Primary path: `loadFromIndex()` reads every `META-INF/singularitylib/auto-register-index.properties` on the classpath (compile-time index from `singularitylib-processor`), instantiates each listed class and classifies into `SimpleCommand` / `CustomItem` / `Listener`. Fallback: deprecated `scanAndCollect(package)` uses Reflections for legacy jars without an index. Cross-plugin dedupe via `ALREADY_INSTANTIATED` (first plugin wins — the lib is a bootstrap plugin with `join-classpath: true`, so shared classes must not double-register); `resetDedupe()` exists for tests.

Tests live in `src/test/java` (`CorePluginTest`, `CommonTest`, `ItemCreatorMetaTest`, `TestPlugin`). Coverage is minimal — don't assume green tests mean full safety.

## Conventions
- Plain Java (no Kotlin anywhere in this repo).
- Public API classes get full Javadoc — keep that up when editing them (javadoc jar is built via maven-javadoc-plugin).
- Version bumps happen through release tags; `-SNAPSHOT` between releases.

## Known issues / tech debt (observed)
1. **Version history**: `pom.xml` is now `2.0.0-SNAPSHOT` (v2 line); tags stop at `1.3.3` while `RELEASE_NOTES.md` describes a "2.2.0" initial release. Reconcile release notes before tagging v2.
2. **Legacy color API**: `org.bukkit.ChatColor` used across `Console`, `Register`, etc. — deprecated in modern Paper (use Adventure `Component`/`NamedTextColor`).
3. **Typo'd enum** `PersisDataType` — public API surface, renaming is breaking.
4. ~~**Reflections scanner** (`Register`) is startup-slow and fragile under complex classpaths/relocation~~ **resolved**: `@AutoRegister` now uses a compile-time index from the `singularitylib-processor` annotation processor (see `plugin/register/Register.java`), with the Reflections scan kept only as a deprecated fallback for legacy jars.
5. **Ships as a plugin AND a library**: `paper-plugin.yml` makes the lib itself a plugin (`main: ...api.Plugin`). Consumers instead depend on it as a Maven dep and extend `CorePlugin`. This dual role is a design smell worth revisiting during the rework.
6. ~~JitPack coordinates mismatch~~ resolved on `rework/v2`: the JitPack repo and the last JitPack-hosted dependency (`com.github.Pinont:Singularity-DevTool`) were removed from the pom. README still shows a JitPack badge/install snippet — update docs when GitHub Packages publishing goes live.
7. Minimal tests; no CI matrix across MC versions.

## Agent guidance
- **Do not break `CorePlugin`'s public contract** (`onPluginStart`/`onPluginStop`, `getConfigManager`, static accessors) — both sibling repos and unknown external plugins depend on it.
- Treat all classes under `api/` as public API; check downstream consumers (sibling repos, GitHub Packages) before renaming/moving anything.
- New features go in the matching `api/` subpackage with Javadoc; wire global behavior through `CorePlugin`/`Register`, not static singletons where avoidable.
- When touching items/events, remember the internal listeners in `plugin/listener` are the machinery behind the public events.
- The owner plans a **major rework**: LTS/rolling MC version support, wiki + javadoc site. Favor changes that reduce coupling to a single Paper version (e.g., isolate NMS/version-specific code) over quick hacks.
