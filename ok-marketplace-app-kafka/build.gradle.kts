plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.bmuschko.docker.java.application)
}

application {
    mainClass.set("ru.otus.otuskotlin.marketplace.app.kafka.MainKt")
}

docker {
    javaApplication {
        mainClassName.set(application.mainClass.get())
        baseImage.set("openjdk:17")
        maintainer.set("(c) Otus")
//        ports.set(listOf(8080))
        val imageName = project.name
        images.set(
            listOf(
                "$imageName:${project.version}",
                "$imageName:latest"
            )
        )
        jvmArgs.set(listOf("-Xms256m", "-Xmx512m"))
    }
}

dependencies {
    implementation(libs.kafka.clients)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.atomicfu)

    // log
    implementation(libs.logback)
    implementation(libs.kotlin.logging)

    // transport models
    implementation(project(":ok-marketplace-common"))
    implementation(project(":ok-marketplace-api-v1-jackson"))
    implementation(project(":ok-marketplace-api-v2-kmp"))
    implementation(project(":ok-marketplace-mappers-v1"))
    implementation(project(":ok-marketplace-mappers-v2"))
    // logic
    implementation(project(":ok-marketplace-biz"))
    // other
    implementation(project(":ok-marketplace-lib-logging-logback"))

    testImplementation(kotlin("test-junit"))
}
