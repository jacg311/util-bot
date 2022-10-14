import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("com.github.johnrengelman.shadow")
    id("io.gitlab.arturbosch.detekt")
}

group = "util-bot"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()

    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

    maven { url = uri("https://maven.kotlindiscord.com/repository/maven-public/") }

    maven { url = uri("https://maven.fabricmc.net/") }
}

dependencies {
    detektPlugins(libs.detekt)

    implementation(libs.kord.extensions)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kx.ser)

    // Logging dependencies
    implementation(libs.groovy)
    implementation(libs.jansi)
    implementation(libs.logback)
    implementation(libs.logging)
}

application {
    // This is deprecated, but the Shadow plugin requires it
    mainClassName = "utilbot.AppKt"
}

tasks.withType<KotlinCompile> {
    // Current LTS version of Java
    kotlinOptions.jvmTarget = "11"

    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "utilbot.AppKt"
        )
    }
}

tasks.shadowJar {
    minimize {
        exclude(dependency("ch.qos.logback:logback-classic"))
    }
}

java {
    // Current LTS version of Java
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

detekt {
    buildUponDefaultConfig = true
    config = rootProject.files("detekt.yml")
}
