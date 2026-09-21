plugins {
    id("java")
    id("idea")
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
}

dependencies {
    implementation(project(":sanctionmanager-api"))
    compileOnly("io.github.vlouboos:serverbridge-api:1.1")
    compileOnly("io.github.vlouboos:standaloneevent-api:1.5")
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("redis.clients:jedis:8.0.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
}

tasks.processResources {
    filesMatching("bungee.yml") {
        expand("projectVersion" to project.version)
    }
}

tasks.test {
    useJUnitPlatform()
}