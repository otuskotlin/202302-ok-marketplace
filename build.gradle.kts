import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm")
}

allprojects {
    group = "ru.otus.otuskotlin.marketplace"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<KotlinJvmCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
