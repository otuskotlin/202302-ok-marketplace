import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

subprojects {
    group = "ru.otus"
    version = "1.0-SNAPSHOT"

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
