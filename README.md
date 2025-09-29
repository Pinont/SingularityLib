# SingularityLib

[![](https://jitpack.io/v/Pinont/SingularityLib.svg)](https://jitpack.io/#Pinont/SingularityLib) ![GitHub Tag](https://img.shields.io/github/v/tag/pinont/singularitylib)
[![license](https://img.shields.io/github/license/pinont/singularitylib)](https://github.com/Pinont/SingularityLib/blob/main/LICENSE)

A fork of [ExperienceLib](https://github.com/pinont/ExperienceLib)

> ## ⚠️ Disclaimer
> This project is still in development, so expect some bugs and missing features. If you find any bugs or have any feature requests, please open an issue on the [GitHub repository](https://github.com/Pinont/SingularityLib/issues).

---

A Minecraft plugin API that provides a lot of benefits to develop Minecraft plugins much easier.

## Features

- Better Command registration
- Better Config Creator
- Better GUI creation
- Better Item Configuration, item interaction, item locking and unique item management
- Better Entity Configuration and Storing
- Auto Register for cleaner code and management
- Database support (MySQL)
- Discord JDA Bootstrap (SlashCommands)

## Future Plans

- [ ] More GUI features (Paginated GUI, Animated GUI)
- [ ] More Item features (Item Enchantments, Item Attributes)
- [ ] More Entity features (Custom Entities, Entity Attributes)
- [ ] More Command features (SubCommands, Command Aliases)
- [ ] Custom Crafting Recipes
- [ ] Discord JDA support (TextCommands, Listeners)
- [ ] More database support (SQLite, MongoDB)

---

## Getting the Latest Version

Go to the JitPack page: [JitPack - SingularityLib](https://jitpack.io/#Pinont/SingularityLib)  
Select a release tag (recommended) or a commit hash (for bleeding edge).  
Replace `Tag` in the examples below with that value.

---

## Installation (Maven)

```xml
<project>
  ...
  <repositories>
    <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>com.github.Pinont</groupId>
      <artifactId>SingularityLib</artifactId>
      <version>Tag</version>
    </dependency>
  </dependencies>
  ...
</project>
```

### Shading / Relocation (Maven)

To avoid dependency conflicts with other plugins, it is highly recommended to shade (relocate) the SingularityLib dependency:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-shade-plugin</artifactId>
      <version>3.2.4</version>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>shade</goal>
          </goals>
          <configuration>
            <relocations>
              <relocation>
                <pattern>com.github.pinont.singularitylib</pattern>
                <shadedPattern>your.unique.package.singularitylib</shadedPattern>
              </relocation>
            </relocations>
            <minimizeJar>true</minimizeJar>
          </configuration>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

---

## Installation (Gradle)

### Groovy DSL (`build.gradle`)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // Replace Tag with a release tag or commit hash
    implementation 'com.github.Pinont:SingularityLib:Tag'
}
```

### Kotlin DSL (`build.gradle.kts`)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Replace Tag with a release tag or commit hash
    implementation("com.github.Pinont:SingularityLib:Tag")
}
```

### Shading / Relocation (Gradle – Shadow Plugin)

You should relocate the library to your own namespace to avoid conflicts.

Groovy DSL (`build.gradle`):
```groovy
plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

dependencies {
    implementation 'com.github.Pinont:SingularityLib:Tag'
}

shadowJar {
    relocate 'com.github.pinont.singularitylib', 'your.unique.package.singularitylib'
    // Optional: remove unused classes
    minimize()
}

build.dependsOn shadowJar
```

Kotlin DSL (`build.gradle.kts`):
```kotlin
plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation("com.github.Pinont:SingularityLib:Tag")
}

tasks.shadowJar {
    relocate("com.github.pinont.singularitylib", "your.unique.package.singularitylib")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
```

After building, use the shaded JAR (often suffixed with `-all.jar` depending on your Shadow plugin version/config) as your plugin artifact.

---

## Example Multi-Module (Gradle)

If you have a parent plugin and a separate module for shared logic:

`settings.gradle(.kts)` includes all modules. Add the dependency only where needed:

```kotlin
dependencies {
    implementation(project(":shared"))
    implementation("com.github.Pinont:SingularityLib:Tag")
}
```

Relocation still belongs in the final (root plugin) module that produces the plugin JAR.

---

## Usage

Your plugin's main class must extend `CorePlugin` instead of `JavaPlugin`.

```java
public class Main extends CorePlugin {

    @Override
    public void onPluginStart() {
        // plugin start logic goes here
        // e.g., register commands, load configs
    }

    @Override
    public void onPluginStop() {
        // plugin stop logic goes here
    }
}
```

### Typical Structure

```
src/main/java/
  your/package/plugin/Main.java
  your/package/plugin/commands/...
  your/package/plugin/listeners/...
  your/package/plugin/config/...
resources/
  plugin.yml
  configs/
```

### plugin.yml Example

```yaml
name: YourPlugin
version: 1.0.0
main: your.package.plugin.Main
api-version: 1.20
author: You
```

---

## Recommended Relocation Pattern

Choose a namespace unique to your organization or plugin to avoid collisions:

```
your.root.package.singularitylib
```

Do NOT skip relocation if you distribute the plugin publicly.

---

## Troubleshooting

| Issue | Cause | Fix |
|-------|-------|-----|
| ClassNotFoundException | Forgot to shade dependency | Ensure Shade/Shadow is configured |
| Version not resolving | Wrong tag / cache issue | Use an explicit release tag; try `./gradlew --refresh-dependencies` or `mvn -U clean install` |
| Duplicate classes | Missing relocation | Add relocation to Shade/Shadow config |
| Plugin loads but features fail | Initialization order | Ensure logic is inside `onPluginStart()` not constructor |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/awesome`
3. Commit changes: `git commit -m "Add awesome feature"`
4. Push branch: `git push origin feature/awesome`
5. Open a Pull Request

Please include clear descriptions and, if applicable, minimal reproduction steps.

---

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file.

---

## Support / Questions

Open an issue on the [issue tracker](https://github.com/Pinont/SingularityLib/issues) with:
- Description
- Server version
- Library version (tag / commit)
- Stack trace (if any)
- Minimal reproduction steps

---

Happy developing!
