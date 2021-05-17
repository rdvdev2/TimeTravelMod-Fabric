plugins {
    kotlin("jvm") version "1.4.21"
    id("fabric-loom") version "0.5-SNAPSHOT"
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

version = "${findProperty("minecraft_version")}+${findProperty("mod_version")}"
group = findProperty("maven_group")!!

repositories {
    jcenter()
    maven(url = "http://maven.fabricmc.net/") {
        name = "Fabric"
    }
    maven(url = "https://dl.bintray.com/ladysnake/libs") {
        name = "Ladysnake Libs"
    }
    maven(url = "https://www.cursemaven.com") {
        name = "Curse Maven"
        content {
            includeGroup("curse.maven")
        }
    }
    maven(url = "https://server.bbkr.space/artifactory/libs-release") {
        name = "CottonMC"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${findProperty("minecraft_version")}")
    mappings("net.fabricmc:yarn:${findProperty("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${findProperty("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${findProperty("kotlin_adapter_version")}")

    // API Dependencies
    setOf(
            "fabric-api-base",
            "fabric-biome-api-v1",
            "fabric-blockrenderlayer-v1",
            "fabric-command-api-v1",
            "fabric-dimensions-v1",
            "fabric-item-api-v1",
            "fabric-item-groups-v0",
            "fabric-networking-blockentity-v0",
            "fabric-networking-api-v1",
            "fabric-object-builder-api-v1",
            "fabric-renderer-registries-v1",
            "fabric-rendering-v1",
            "fabric-tool-attribute-api-v1"
    ).forEach {
        modImplementation(fabricApi.module(it, findProperty("fabric_version") as String?))
    }
    modApi("me.sargunvohra.mcmods:autoconfig1u:${findProperty("autoconfig1u_version")}") {
        exclude(group = "net.fabricmc.fabric-api")
    }
    modImplementation ("io.github.prospector:modmenu:${findProperty("modmenu_version")}")
    modImplementation ("io.github.cottonmc:LibGui:${findProperty("libgui_version")}")

    // Jar-in-Jar dependencies
    include("curse.maven:DisableCustomWorldsAdvice-401978:${findProperty("disable_custom_worlds_advice_fileid")}")
    include("me.sargunvohra.mcmods:autoconfig1u:${findProperty("autoconfig1u_version")}")
    include("me.shedaniel.cloth:config-2:${findProperty("cloth_config_2_version")}")
    include("io.github.cottonmc:LibGui:${findProperty("libgui_version")}")

    // Dev runtime dependencies
    modRuntime("net.fabricmc.fabric-api:fabric-api:${findProperty("fabric_version")}")
    modRuntime("curse.maven:DisableCustomWorldsAdvice-401978:${findProperty("disable_custom_worlds_advice_fileid")}")
    modRuntime("io.github.prospector:modmenu:${findProperty("modmenu_version")}")
    modRuntime("me.shedaniel.cloth:config-2:${findProperty("cloth_config_2_version")}")
    modRuntime("io.github.cottonmc:LibGui:${findProperty("libgui_version")}")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.getByName<ProcessResources>("processResources"){
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(
            mutableMapOf(
                "version" to project.version
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    this.options.encoding = "UTF-8"

    val targetVersion = 8
    if (JavaVersion.current().isJava9Compatible) {
        this.options.release.set(targetVersion)
    }
}

val jar: org.gradle.jvm.tasks.Jar by tasks
jar.from("LICENSE")