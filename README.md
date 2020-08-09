# TavianRPG [![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen)]()
The core plugin for the RPG gamemode. Adds Custom Attributes, Custom Entities, Custom Items and a Custom Damage system.

## How To
### Build The Plugin:
The plugin requires a custom fork of paper in your maven local which is located [here](https://github.com/AtomIsHere/Paper.git). Once you've done that run `./gradlew shadowJar`.

When developing it's also helpful to have the Lombok Extension for your respected IDE.
### Run The Plugin:
The plugin has no (current) dependencies. It's required you have Java 8+ and will not work on versions bellow that, although when using Java 9+ you have to add `--add-opens java.base/jdk.internal.reflect=ALL-UNNAMED` as a VM option.