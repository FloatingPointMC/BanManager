plugins {
    id("java")
}

group = "io.github.floatingpointmc"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
repositories {
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    implementation(project(":sanctionmanager-api"))
    implementation(project(":sanctionmanager-core"))
    compileOnly("com.velocitypowered:velocity-api:4.2.0")
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("org.bstats:bstats-velocity:3.2.1")
    implementation("org.incendo:cloud-velocity:2.0.0-beta.10")
}

tasks.test {
    useJUnitPlatform()
}