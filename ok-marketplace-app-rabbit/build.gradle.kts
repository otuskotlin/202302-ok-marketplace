plugins {
    alias(libs.plugins.kotlin.jvm)
    java
    alias(libs.plugins.bmuschko.docker.java.application)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.amqp.client)
    implementation(libs.jackson.databind)
    implementation(libs.logback)
    implementation(libs.kotlinx.coroutines.core)

    // transport models common
    implementation(project(":ok-marketplace-common"))

    // v1 api
    implementation(project(":ok-marketplace-api-v1-jackson"))
    implementation(project(":ok-marketplace-mappers-v1"))

    // v2 api
    implementation(project(":ok-marketplace-mappers-v2"))
    implementation(project(":ok-marketplace-api-v2-kmp"))

    implementation(project(":ok-marketplace-biz"))
    // other
    implementation(project(":ok-marketplace-lib-logging-logback"))

    testImplementation(libs.testcontainers)
    testImplementation(kotlin("test"))
    testImplementation(project(":ok-marketplace-stubs"))
}



docker {
    javaApplication {

        baseImage.set("openjdk:17")
        ports.set(listOf(8080,5672))
        images.set(setOf("${project.name}:latest"))
        jvmArgs.set(listOf("-XX:+UseContainerSupport"))
    }

}



