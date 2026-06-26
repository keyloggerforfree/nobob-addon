@file:Suppress("UnstableApiUsage", "PropertyName")

import org.polyfrost.gradle.util.noServerRunConfigs
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

// Adds support for kotlin, and adds the Polyfrost Gradle Toolkit
// which we use to prepare the environment.
plugins {
    kotlin("jvm")
    id("org.polyfrost.multi-version")
    id("org.polyfrost.defaults.repo")
    id("org.polyfrost.defaults.java")
    id("org.polyfrost.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.2"
    java
}

// Gets the mod name, version and id from the `gradle.properties` file.
val mod_name: String by project
val mod_version: String by project
val mod_id: String by project
val mod_archives_name: String by project

// Replaces the @VER@/@NAME@/@ID@ tokens in NoBob.java with values from gradle.properties.
blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

// Sets the mod version to the one specified in `gradle.properties`. Make sure to change this following semver!
version = mod_version
// Sets the group to match the mod's base package.
group = "net.chinesespyware"

// Sets the name of the output jar.
base {
    archivesName.set("$mod_archives_name-$platform")
}

// Java version the target Minecraft version requires (8 for 1.8.9 / 1.12.2).
val targetJavaVersion = when {
    platform.mcMinor >= 18 -> 17
    platform.mcMinor == 17 -> 16
    else -> 8
}

// Compile to the correct bytecode level for the target Minecraft version.
// Note: Gradle does NOT apply the java toolchain to JavaExec tasks (like runClient),
// so the run JVM is pinned separately below via javaLauncher.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

// Pin runClient (and any other JavaExec) to the same Java version. Without this,
// JavaExec tasks use the Gradle daemon JVM (the IDE's JBR/Java 17+), which causes
// LaunchWrapper to crash: it casts the system classloader to URLClassLoader, which
// only holds on Java 8. Uses configureEach (lazy) to avoid realizing tasks early.
val gameLauncher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}
tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(gameLauncher)
}

// Configures Polyfrost Loom.
loom {
    // Removes the server configs from IntelliJ IDEA, leaving only client runs.
    noServerRunConfigs()

    // Adds the tweak class if we are building a legacy version of Forge.
    if (project.platform.isLegacyForge) {
        runConfigs {
            "client" {
                programArgs("--tweakClass", "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker")
            }
        }
    }
}

// Creates the shade/shadow configuration, so we can include libraries inside our mod.
val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

// Configures the output directory for resources, and adds the dummy sourceSet so
// OneConfig is available at compile time without being bundled into the output jar.
sourceSets {
    val dummy by creating

    main {
        output.setResourcesDir(java.classesDirectory)
        compileClasspath += dummy.output
    }
}

// Adds the Polyfrost maven repository.
repositories {
    maven("https://repo.polyfrost.org/releases")
}

// Configures the libraries/dependencies for the mod.
dependencies {
    // Adds the OneConfig library; the dummy config keeps it off the runtime classpath.
    "dummyCompileOnly"("cc.polyfrost:oneconfig-$platform:0.2.2-alpha+")
    modCompileOnly("cc.polyfrost:oneconfig-$platform:0.2.2-alpha+")

    // Adds DevAuth for logging in to Minecraft in development.
    modRuntimeOnly("me.djtheredstoner:DevAuth-${if (platform.isFabric) "fabric" else if (platform.isLegacyForge) "forge-legacy" else "forge-latest"}:1.2.0")

    // For legacy Forge: include the OneConfig launch wrapper.
    if (platform.isLegacyForge) {
        shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
    }
}

tasks {
    // Processes mcmod.info and replaces template variables.
    processResources {
        inputs.property("id", mod_id)
        inputs.property("name", mod_name)
        val java = if (project.platform.mcMinor >= 18) {
            17
        } else {
            if (project.platform.mcMinor == 17) 16 else 8
        }
        val compatLevel = "JAVA_${java}"
        inputs.property("java", java)
        inputs.property("java_level", compatLevel)
        inputs.property("version", mod_version)
        inputs.property("mcVersionStr", project.platform.mcVersionStr)
        filesMatching(listOf("mcmod.info", "mods.toml")) {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr
                )
            )
        }
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "id" to mod_id,
                    "name" to mod_name,
                    "java" to java,
                    "java_level" to compatLevel,
                    "version" to mod_version,
                    "mcVersionStr" to project.platform.mcVersionStr.substringBeforeLast(".") + ".x"
                )
            )
        }
    }

    // Exclude loader-specific resource files from the wrong loader's jar.
    withType(Jar::class.java) {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml")
            } else {
                exclude("mcmod.info")
            }
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }

    jar {
        if (platform.isLegacyForge) {
            manifest.attributes += mapOf(
                "ModSide" to "CLIENT",
                "ForceLoadAsMod" to true,
                "TweakOrder" to "0",
                "TweakClass" to "cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker"
            )
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
}
