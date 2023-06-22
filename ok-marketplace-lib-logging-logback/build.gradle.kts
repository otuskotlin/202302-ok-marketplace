plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation(project(":ok-marketplace-lib-logging-common"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)

    // logback
    implementation(libs.logback.logstash)
//    implementationlibs.logback.kafka)
    implementation(libs.janino)
    api(libs.logback)

    testImplementation(kotlin("test-junit"))
}
