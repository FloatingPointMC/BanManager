pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "SanctionManager"
include("sanctionmanager-api")
include("sanctionmanager-spigot")
include("sanctionmanager-core")
include("sanctionmanager-bungee")