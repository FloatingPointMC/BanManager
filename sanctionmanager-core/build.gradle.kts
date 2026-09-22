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
    compileOnly("org.jetbrains:annotations:26.1.0")
    annotationProcessor("org.jetbrains:annotations:26.1.0")
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
    implementation("redis.clients:jedis:8.0.1")
    implementation("com.zaxxer:HikariCP:4.0.3")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}