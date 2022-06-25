# BrickI18n

A Minecraft library that makes localization easier than ever.

## Platforms

* [x] Minestom
* [x] Spigot / Paper

## Install

Get the [release](https://github.com/GufliMC/BrickI18n/releases) and put it in your plugins/extensions folder.

## API

### Gradle

```
repositories {
    maven { url "https://repo.jorisg.com/snapshots" }
}
```

```
dependencies {
    // minestom
    compileOnly 'com.guflimc.brick.i18n:minestom-api:+'
    
    // spigot
    compileOnly 'com.guflimc.brick.i18n:spigot-api:+'
}
```

### Javadocs

You can find the javadocs for all platforms [here](https://guflimc.github.io/BrickI18n)

### Examples

```java
// Initialize
SpigotNamespace namespace = new SpigotNamespace(this, Locale.ENGLISH);
namespace.loadValues(namespace, "languages");

SpigotI18nAPI.get().register(namespace); // easy access later

// Usage
namespace.send(player, "welcome", sender.getName());

SpigotI18nAPI.get().byId("PluginName").send(player, "welcome", sender.getName());
```

resources/languages/en.json
```json
{
    "welcome": "Welcome to the server {0}!",
}
```
