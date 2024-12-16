plugins {
    id("java")
    id("java-library")
    kotlin("jvm") version "1.9.23"

    id("dev.architectury.loom") version ("1.7-SNAPSHOT")
    id("architectury-plugin") version ("3.4-SNAPSHOT")

    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "${property("group")}"
version = "${property("mod_version")}"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    enableTransitiveAccessWideners.set(true)
    silentMojangMappingsLicense()

}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://cursemaven.com")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")

    maven("https://maven.blamejared.com/")
    maven("https://modmaven.dev")

}

dependencies {
    minecraft("net.minecraft:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    // Forge
    forge("net.minecraftforge:forge:${property("forge_version")}")

    modImplementation("com.cobblemon:forge:${property("cobblemon_version")}")

    implementation("thedarkcolour:kotlinforforge:4.4.0")

//    // compile against the JEI API but do not include it at runtime
//    compileOnly("mezz.jei:jei-${property("minecraft_version")}-forge-api:${property("jei_version")}")
//    // at runtime, use the full JEI jar for Forge
//    runtimeOnly("mezz.jei:jei-${property("minecraft_version")}-forge:${property("jei_version")}")


}

tasks.processResources {
    filesMatching("META-INF/mods.toml") {
        expand(
            mapOf(
                "author" to project.property("author"),
                "mod_name" to project.property("mod_name"),
                "mod_id" to project.property("mod_id"),
                "mod_version" to project.property("mod_version"),
                "forge_version_range" to project.property("forge_version_range"),
                "loader_version_range" to project.property("loader_version_range"),
                "minecraft_version_range" to project.property("minecraft_version_range"),
                "mod_description" to project.property("mod_description"),
                "mod_license" to project.property("mod_license")
            )
        )
    }
}

tasks {
    base.archivesName.set("${project.property("archives_base_name")}-forge")
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    jar.get().archiveClassifier.set("dev")
}