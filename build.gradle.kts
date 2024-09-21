import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.2.4"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
    id("io.freefair.lombok") version "8.6"
}

group = "net.slqmy"
version = "1.0"
description = "A Minecraft Java plugin that adds the Castle Siege minigame."

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    implementation("org.projectlombok:lombok:1.18.30")
    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    jar {
        destinationDirectory.set(file("../../CastleSiegeTest/plugins"))
    }
}

bukkitPluginYaml {
    main = "net.slqmy.castle_siege_plugin.CastleSiegePlugin"
    prefix = "Castle Siege"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.addAll("rolyPolyVole", "Slqmy")
    apiVersion = "1.20.5"
    commands.register("start")
    commands.register("end")
    commands.register("debug")
}