plugins {
    id("java")
    id("idea")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.github.floatingpointmc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/repository/public/") }
}

dependencies {
    implementation(project(":sanctionmanager-api"))
    implementation(project(":sanctionmanager-core"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("org.bstats:bstats-bukkit:3.2.1")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("projectVersion" to project.version)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
    relocate("redis.clients.jedis", "io.github.floatingpointmc.banmanager.libs.jedis")
    relocate("com.zaxxer.hikari", "io.github.floatingpointmc.banmanager.libs.hikari")
    relocate("org.bstats", "io.github.floatingpointmc.banmanager.libs.bstats")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}