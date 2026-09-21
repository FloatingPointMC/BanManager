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
}

tasks.test {
    useJUnitPlatform()
}