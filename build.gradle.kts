plugins {
    id("java")
    id("dev.architectury.loom") version("1.7-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version "1.9.23"
}


group = "org.example"
version = "1.0-SNAPSHOT"

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://hub.spigotmc.org/nexus/content/groups/public/")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    minecraft("net.minecraft:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    forge("net.minecraftforge:forge:${property("forge_version")}")

    modImplementation("com.cobblemon:forge:${property("cobblemon_version")}")
    //Needed for cobblemon
    implementation("thedarkcolour:kotlinforforge:4.5.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}