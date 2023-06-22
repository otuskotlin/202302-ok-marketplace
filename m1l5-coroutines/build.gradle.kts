plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val coroutinesVersion: String by project
val jacksonVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.jackson.module.kotlin) // from string to object

    implementation("com.squareup.okhttp3:okhttp:4.9.3") // http client

    testImplementation(kotlin("test-junit"))
}
