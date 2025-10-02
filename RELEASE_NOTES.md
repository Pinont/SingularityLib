### SingularityLib 2.2.0

#### Overview
SingularityLib is a Minecraft plugin API that accelerates plugin development by offering refined abstractions for commands, configuration, GUIs, items, entities, automatic component registration, database integration, and Discord JDA bootstrap support.

Although this is labeled 2.2.0, this marks the first formally published ("initial") public release tag in the current release cycle. Future releases will follow semantic versioning once APIs stabilize further.

> Still under active development. Please report bugs or feature requests via Issues.

#### Key Feature Areas (Current Scope)
- Command System: Cleaner registration and future-ready structure for subcommand expansion.
- Configuration Utilities: Streamlined config creation & structured access patterns.
- GUI Toolkit: Base GUI creation (pagination & animation planned).
- Item Framework: Configurable item definitions, interaction hooks, unique item management, item locking.
- Entity Utilities: Configuration + storage helpers.
- Auto-Registration: Reduced boilerplate for services/components.
- Database Layer: MySQL support groundwork.
- Discord Integration: JDA bootstrap with Slash Command support.

#### Roadmap / Planned Enhancements
- Paginated & Animated GUIs
- Item enchantment & attribute enrichment
- Custom entities & expanded entity attribute APIs
- Subcommand & alias DSL for commands
- Custom crafting recipe registration
- Discord Text Commands & Event Listeners
- Additional database backends (SQLite, MongoDB)

#### Installation (Maven)
```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>

<dependency>
  <groupId>io.github.pinont</groupId>
  <artifactId>SingularityLib</artifactId>
  <version>2.2.0</version>
</dependency>
```

#### Installation (Gradle – Groovy)
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'io.github.pinont:SingularityLib:2.2.0'
}
```

#### Installation (Gradle – Kotlin DSL)
```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.github.pinont:SingularityLib:2.2.0")
}
```

#### Basic Usage
```java
public class Main extends CorePlugin {
    @Override
    public void onPluginStart() {
        // initialization logic
    }
    @Override
    public void onPluginStop() {
        // cleanup logic
    }
}
```

#### Suggested Adoption Flow
1. Add dependency & validate server startup.
2. Migrate base commands into the library’s command system.
3. Refactor configs to the standardized config creator.
4. Introduce GUI abstractions incrementally (prepare for pagination in future release).
5. Integrate item/unique item logic where duplication or locking matters.
6. Provide feedback on pain points → open issues.

#### Compatibility Notes
- If you are upgrading from earlier untagged snapshots, verify any refactors in command or item APIs.
- Future 2.x releases may adjust GUI and command internals before an API freeze (targeting a later 2.x or 3.0 milestone).
- Database abstraction may shift when new backends are introduced.

#### Forward-Looking (Potential Breaking Areas)
- GUI API (pagination/animation hooks)
- Item attribute/enchantment extension points
- Entity abstraction layering
- Command alias/subcommand registration semantics
- Database interface shape for multi-backend support

#### Changelog (Initial Tag)
All features are new for this first public tagged release.
- Core abstractions (commands, configs, GUI base, items, entities)
- Unique item & locking handling
- Auto-registration system
- MySQL integration baseline
- Discord JDA slash command bootstrap

#### Contributing
PRs and issues welcome. Focus areas needing feedback:
- Command API ergonomics
- Desired GUI patterns (animation, pagination UX)
- Item metadata extension needs
- Database extensibility expectations

#### Looking Ahead (Short-Term Priorities)
1. GUI pagination & animation
2. Subcommand & alias DSL
3. Item attribute/enchantment composability
4. SQLite backend integration
5. Discord listener/event pipeline

---
Thank you for trying SingularityLib! Your feedback will directly shape upcoming releases.