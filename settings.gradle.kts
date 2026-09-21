pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "SanctionManager"
include("sanctionmanager-api")
include("sanctionmanager-spigot")
include("sanctionmanager-core")
include("sanctionmanager-bungee")
include("sanctionmanager-velocity")