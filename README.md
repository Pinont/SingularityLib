# SingularityLib

[![](https://img.shields.io/github/license/pinont/singularitylib)](https://github.com/Pinont/SingularityLib/blob/main/LICENSE) [![](https://img.shields.io/maven-central/v/io.github.pinont/singularitylib)](https://central.sonatype.com/artifact/io.github.pinont/singularitylib) [![](https://github.com/Pinont/SingularityLib/actions/workflows/build.yml/badge.svg)](https://github.com/Pinont/SingularityLib/actions/workflows/build.yml) [![JavaDoc](https://img.shields.io/badge/docs-JavaDoc-e0a54b)](https://maven.pinont.me/javadoc/singularitylib/)

**Docs / JavaDoc:** [maven.pinont.me/javadoc/singularitylib](https://maven.pinont.me/javadoc/singularitylib/) · [latest API](https://maven.pinont.me/javadoc/singularitylib/latest/) · [javadoc.io fallback](https://javadoc.io/doc/io.github.pinont/singularitylib/2.0.0)

A fork of [ExperienceLib](https://github.com/pinont/ExperienceLib)

> ## ⚠️ Disclaimer
> This project is still in development, so expect some bugs and missing features. If you find any bugs or have any feature requests, please open an issue on the [GitHub repository](https://github.com/Pinont/SingularityLib/issues)

---

A Minecraft plugin api that provides a lot of benefit to develop Minecraft plugin much easier

## Features

- Better Command registration
- Better Config Creator
- Better GUI creation
- Better Item Configuration, item interaction, item locking and unique item management
- Better Entity Configuration and Storing
- Auto Register for cleaner code and management
- Database support (MYSQL)
- Discord JDA Boostrap (SlashCommands)

## Future Plans
- [ ] More GUI features (Paginated GUI, Animated GUI)
- [ ] More Item features (Item Enchantments, Item Attributes)
- [ ] More Entity features (Custom Entities, Entity Attributes)
- [ ] More Command features (SubCommands, Command Aliases)
- [ ] Custom Crafting Recipes
- [ ] Discord JDA support (TextCommands, Listeners)
- [ ] More database support (SQLite, MongoDB)

## Installation (Maven)

SingularityLib is published to **Maven Central** — no repository block needed for releases:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.pinont</groupId>
    <artifactId>singularitylib</artifactId>
    <version>2.0.0</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

🚀 **Get the latest version:** [central.sonatype.com/artifact/io.github.pinont/singularitylib](https://central.sonatype.com/artifact/io.github.pinont/singularitylib)

### Snapshots (dev/pre-release)

```xml
<repositories>
  <repository>
    <id>singularity</id>
    <url>https://maven.pinont.me</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>io.github.pinont</groupId>
    <artifactId>singularitylib</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

### Bootstrap plugin model ⚠️ important
The lib runs as **its own server plugin**; consumer plugins compile against it with
`provided` scope and declare it in `paper-plugin.yml`. Never shade the lib into your jar:

```yaml
# paper-plugin.yml (consumer)
dependencies:
  server:
    SingularityLib:
      load: BEFORE
      required: true
      join-classpath: true
```

## Usage
To use SingularityLib, your main class must extend `CorePlugin` instead of `JavaPlugin`.
```java
public class Main extends CorePlugin {
    @Override
    public void onPluginStart() {
        // plugin start logic goes here
    }
    @Override
    public void onPluginStop() {
        // plugin stop logic goes here
    }
}
```

### ⚡ Compile-time `@AutoRegister` index (recommended)
Annotation-marked classes are collected **at compile time** by the
`singularitylib-processor` annotation processor and shipped inside your jar as
`META-INF/singularitylib/auto-register-index.properties`. At plugin startup
`Register` reads that index instead of scanning the classpath with Reflections —
no startup cost, no fragile classpath assumptions.

Mark your components with `@AutoRegister`:

```java
@AutoRegister
public class MyListener implements Listener { /* … */ }

@AutoRegister
public class MyCommand implements SimpleCommand { /* … */ }
```

Then tell your build to run the processor. Using Maven with
`annotationProcessorPaths` (the processor is **not** on the runtime classpath —
only the index resource matters at runtime):

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <configuration>
        <annotationProcessorPaths>
          <annotationProcessorPath>
            <groupId>io.github.pinont</groupId>
            <artifactId>singularitylib-processor</artifactId>
            <version>2.0.0</version>
          </annotationProcessorPath>
        </annotationProcessorPaths>
      </configuration>
    </plugin>
  </plugins>
</build>
```

How it works: a class annotated with `@AutoRegister` is picked up by the
processor during compilation, its fully-qualified name is appended to the index,
and at runtime `CorePlugin.onEnable()` calls `Register.loadFromIndex()` which
reads every index on the classpath, instantiates each listed class via its
no-arg constructor, and registers it as a listener / command / custom item.

> **Backward compatibility:** consumers compiled *without* the processor (no
> index in their jar) fall back to the old (deprecated) Reflections
> `scanAndCollect(package)` path, so nothing breaks on upgrade. The
> `org.reflections` dependency is only needed for that fallback.
