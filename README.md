# NoBob

A Minecraft mod that makes the view bobbing animation more subtle by suppressing it entirely when enabled. Built with [OneConfig](https://github.com/Polyfrost/OneConfig) v1 for Minecraft 1.8.9 on [Ornithe](https://ornithemc.net/).

Toggle it on or off from the OneConfig menu.

Requires OneConfig, Fabric Language Kotlin, Compose Multiplatform and OSL. The Forge 1.8.9 / 1.12.2 build (OneConfig v0) lives on the `legacy-v0-forge` branch.

## Building

Requires JDK 25 (Gradle's toolchain resolver can download one).

```sh
./gradlew build
```

The jar ends up in `build/libs/`.
