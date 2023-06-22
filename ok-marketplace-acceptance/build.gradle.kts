plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.ktor.client.okhttp.jvm)

    implementation(project(":ok-marketplace-api-v1-jackson"))
    implementation(project(":ok-marketplace-api-v2-kmp"))

    implementation(libs.logback)
    implementation(libs.kotlin.logging)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.property)

    implementation(libs.amqp.client)
    implementation(libs.kafka.clients)

    testImplementation(libs.testcontainers)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.ktor.client.core)
    testImplementation(libs.ktor.client.okhttp)
}

var severity: String = "MINOR"

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
        dependsOn(":ok-marketplace-app-spring:dockerBuildImage")
        dependsOn(":ok-marketplace-app-ktor:publishImageToLocalRegistry")
        dependsOn(":ok-marketplace-app-rabbit:dockerBuildImage")
        dependsOn(":ok-marketplace-app-kafka:dockerBuildImage")
    }
}
