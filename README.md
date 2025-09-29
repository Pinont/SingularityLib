# SingularityLib

A fork of [ExperienceLib](https://github.com/pinont/ExperienceLib)


> ## ⚠️ Disclaimer
> This project is still in development, so expect some bugs and missing features. If you find any bugs or have any feature requests, please open an issue on the [GitHub repository](https://github.com/Pinont/SingularityLib/issues)

[![](https://jitpack.io/v/Pinont/SingularityLib.svg)](https://jitpack.io/#Pinont/SingularityLib) ![GitHub Tag](https://img.shields.io/github/v/tag/pinont/singularitylib)
[![license](https://img.shields.io/github/license/pinont/singularitylib)](https://github.com/Pinont/SingularityLib/blob/main/LICENSE) 

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

If the library is published to GitHub Packages, add the GitHub repository and dependency to your `pom.xml`:

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
	    <groupId>com.github.pinont</groupId>
	    <artifactId>SingularityLib</artifactId>
	    <version>Tag</version>
	</dependency>
  </dependencies>
  ...
</project>
```
### Shading Plugin (Important)
To avoid dependency conflicts with other plugins, it is highly recommended to shade the SingularityLib dependency
using the Maven Shade Plugin. Here is an example configuration:

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
