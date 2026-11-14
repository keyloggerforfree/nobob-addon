import net.ornithemc.ploceus.api.PloceusGradleExtensionApi

// OneConfig v1 targets 1.8.9 through Ornithe: Fabric Loader running on legacy
// Minecraft, with Ornithe's Feather mappings applied by the Ploceus plugin.
// Build setup follows Polyfrost's own Ornithe mods (e.g. DamageTint).
plugins {
    java
    id("net.fabricmc.fabric-loom-remap") version "1.19-SNAPSHOT"
    id("ploceus") version "1.17.4"
    id("dev.deftu.gradle.bloom") version "0.2.0"
}

val mod_name: String by project
val mod_id: String by project
val mod_version: String by project
val mod_group: String by project
val minecraft_version: String by project
val feather_build: String by project
val fabric_loader_version: String by project
val oneconfig_version: String by project

version = "$mod_version+$minecraft_version-ornithe"
group = mod_group
base.archivesName = mod_name

// OneConfig v1 on Ornithe runs on a modern JVM, and Polyfrost's Ornithe builds
// target Java 25, so we match that.
val requiredJava = JavaVersion.VERSION_25

val ploceus = extensions.getByType<PloceusGradleExtensionApi>().apply {
    setIntermediaryGeneration(2)
}

// Ornithe supplies its own LWJGL; keep vanilla's LWJGL 2 off the classpath.
configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
}

repositories {
    mavenCentral()
    google() // androidx artifacts pulled in by OneConfig's Compose UI
    maven("https://repo.polyfrost.org/releases")
    exclusiveContent {
        forRepository { maven("https://maven.fabricmc.net/") }
        filter { includeGroup("net.fabricmc") }
    }
    // OneConfig depends on pylon, which is only published here.
    exclusiveContent {
        forRepository { maven("https://maven.cloverclient.com/releases") }
        filter { includeGroup("pl.tomgirl") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(ploceus.layeredMappings {
        mappings("net.ornithemc:feather-gen2:$minecraft_version+build.$feather_build:v2") {
            containsUnpick()
        }
    })

    modImplementation("net.fabricmc:fabric-loader:$fabric_loader_version")

    modImplementation("org.polyfrost.oneconfig:$minecraft_version-ornithe:$oneconfig_version")
    for (module in arrayOf("config", "config-impl", "events", "internal", "ui", "utils")) {
        implementation("org.polyfrost.oneconfig:$module:$oneconfig_version")
    }
}

loom {
    runConfigs.all {
        runDirectory = rootProject.file("run")
    }
    runConfigs.remove(runConfigs["server"])
}

java {
    sourceCompatibility = requiredJava
    targetCompatibility = requiredJava
    toolchain {
        languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = requiredJava.majorVersion.toInt()
}

// Replaces the @MOD_*@ tokens in NoBob.java with values from gradle.properties.
bloom {
    replacement("@MOD_ID@", mod_id)
    replacement("@MOD_NAME@", mod_name)
    replacement("@MOD_VERSION@", mod_version)
}

tasks.processResources {
    val props = mapOf(
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_version" to mod_version,
        "minecraft_version" to minecraft_version,
        "loader_version" to fabric_loader_version,
        "oneconfig_version" to oneconfig_version,
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
}
