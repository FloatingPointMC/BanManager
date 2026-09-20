pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "BanManager"
include("banmanager-api")
include("banmanager-spigot")
include("banmanager-core")