plugins {
    kotlin("jvm") version "2.1.0"
}

group = "dev.codebasedlearning.adventofcode.y2023"
version = "1.1.1"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.codebasedlearning:adventofcode_commons:3bd6e22bdc") {
        isChanging = true
    }
    testImplementation(kotlin("test"))
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

    }

    dependencies {
        implementation("com.github.codebasedlearning:adventofcode_commons:3bd6e22bdc") {
            isChanging = true
        }
        testImplementation(kotlin("test"))
    }
}
