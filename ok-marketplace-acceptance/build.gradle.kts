plugins {
    kotlin("jvm")
}

dependencies {
    val testcontainersVersion: String by project
    val kotestVersion: String by project
    val ktorVersion: String by project
    val coroutinesVersion: String by project
    val logbackVersion: String by project
    val kotlinLoggingJvmVersion: String by project
    val ktorClientOkhttpVersion: String by project
    val rabbitVersion: String by project

    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-client-okhttp-jvm:$ktorClientOkhttpVersion")

    implementation(project(":ok-marketplace-api-v1-jackson"))
    implementation(project(":ok-marketplace-api-v2-kmp"))

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingJvmVersion")

    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")

    implementation("com.rabbitmq:amqp-client:$rabbitVersion")

    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    testImplementation("io.ktor:ktor-client-core:$ktorVersion")
    testImplementation("io.ktor:ktor-client-okhttp:$ktorVersion")
}

var severity: String = "MINOR"

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
        dependsOn(":ok-marketplace-app-spring:dockerBuildImage")
        dependsOn(":ok-marketplace-app-ktor:publishImageToLocalRegistry")
        dependsOn(":ok-marketplace-app-rabbit:dockerBuildImage")
    }
}
