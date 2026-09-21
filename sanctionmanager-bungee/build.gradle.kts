plugins {
    id("java")
    id("idea")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.github.floatingpointmc"
version = "1.0-SNAPSHOT"


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/repository/public/") }
    maven { url = uri("https://repo.glaremasters.me/repository/public/") }
}

dependencies {
    implementation(project(":sanctionmanager-api"))
    implementation(project(":sanctionmanager-core"))
    compileOnly("net.md-5:bungeecord-api:26.1-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("org.bstats:bstats-bungeecord:3.2.1")
}

tasks.shadowJar {
    archiveClassifier.set("")
    mergeServiceFiles()
    relocate("redis.clients.jedis", "io.github.floatingpointmc.banmanager.libs.jedis")
    relocate("com.zaxxer.hikari", "io.github.floatingpointmc.banmanager.libs.hikari")
    relocate("org.bstats", "io.github.floatingpointmc.banmanager.libs.bstats")
    minimize()
}

tasks.test {
    useJUnitPlatform()
}