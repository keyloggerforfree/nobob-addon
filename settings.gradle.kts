pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.ornithemc.net/releases")
        maven("https://maven.deftu.dev/releases")
        maven("https://maven.deftu.dev/snapshots") // Bloom
        maven("https://repo.polyfrost.org/releases")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = providers.gradleProperty("mod_name").get()
