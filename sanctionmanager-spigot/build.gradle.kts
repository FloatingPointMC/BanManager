plugins {
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.github.floatingpointmc"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven { url = uri("https://hub.spigotmc.org/nexus/repository/public/") }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation(project(":sanctionmanager-api"))
    implementation(project(":sanctionmanager-core"))
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
    relocate("io.github.vlouboos.serverbridge", "io.github.floatingpointmc.banmanager.libs.serverbridge")
    relocate("io.github.vlouboos.standaloneevent", "io.github.floatingpointmc.banmanager.libs.standaloneevent")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}