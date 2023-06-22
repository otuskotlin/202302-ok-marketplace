@file:Suppress("UNUSED_VARIABLE")

plugins {
    id("application")
    alias(libs.plugins.kotlin.serialization)
    kotlin("multiplatform")
    alias(libs.plugins.ktor)
}

repositories {
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

application {
    mainClass.set("io.ktor.server.cio.EngineMain")
}

ktor {
    docker {
        localImageName.set(project.name + "-ktor")
        imageTag.set(project.version.toString())
        jreVersion.set(io.ktor.plugin.features.JreVersion.JRE_17)
    }
}

jib {
    container.mainClass = "io.ktor.server.cio.EngineMain"
}

kotlin {
    jvm {
        withJava()
    }
    linuxX64 {}
    macosX64 {}
    macosArm64 {}

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries {
            executable {
                entryPoint = "ru.otus.otuskotlin.marketplace.app.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(libs.ktor.core) // "io.ktor:ktor-server-core:$ktorVersion"
                implementation(libs.ktor.cio) // "io.ktor:ktor-server-cio:$ktorVersion"
                implementation(libs.ktor.auth) // "io.ktor:ktor-server-auth:$ktorVersion"
                implementation(libs.ktor.auto.head.response) // "io.ktor:ktor-server-auto-head-response:$ktorVersion"
                implementation(libs.ktor.caching.headers) // "io.ktor:ktor-server-caching-headers:$ktorVersion"
                implementation(libs.ktor.cors) // "io.ktor:ktor-server-cors:$ktorVersion"
                implementation(libs.ktor.websockets) // "io.ktor:ktor-server-websockets:$ktorVersion"
                implementation(libs.ktor.config.yaml) // "io.ktor:ktor-server-config-yaml:$ktorVersion"
                implementation(libs.ktor.content.negotiation) // "io.ktor:ktor-server-content-negotiation:$ktorVersion"
                implementation(libs.ktor.auth) // "io.ktor:ktor-auth:$ktorVersion"

                implementation(project(":ok-marketplace-common"))
                implementation(project(":ok-marketplace-biz"))

                // v2 api
                implementation(project(":ok-marketplace-api-v2-kmp"))
                implementation(project(":ok-marketplace-mappers-v2"))

                // Stubs
                implementation(project(":ok-marketplace-stubs"))

                implementation(project(":ok-marketplace-lib-logging-kermit"))
                implementation(project(":ok-marketplace-api-log1"))
                implementation(project(":ok-marketplace-mappers-log1"))

                implementation(libs.ktor.json)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation(libs.ktor.test.host)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.websockets)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation(libs.ktor.core) // "io.ktor:ktor-server-core:$ktorVersion"
                implementation(libs.ktor.netty) // "io.ktor:ktor-ktor-server-netty:$ktorVersion"

                // jackson
                implementation(libs.ktor.jackson) // io.ktor:ktor-serialization-jackson
                implementation(libs.ktor.content.negotiation) // io.ktor:ktor-server-content-negotiation
                implementation(libs.ktor.json) // io.ktor:ktor-serialization-kotlinx-json

                implementation(libs.ktor.locations)
                implementation(libs.ktor.caching.headers)
                implementation(libs.ktor.call.logging)
                implementation(libs.ktor.auto.head.response)
                implementation(libs.ktor.cors) // "io.ktor:ktor-cors:$ktorVersion"
                implementation(libs.ktor.default.headers) // "io.ktor:ktor-cors:$ktorVersion"

                implementation(libs.ktor.websockets) // "io.ktor:ktor-websockets:$ktorVersion"
                implementation(libs.ktor.auth) // "io.ktor:ktor-auth:$ktorVersion"
                implementation(libs.ktor.auth.jwt) // "io.ktor:ktor-auth-jwt:$ktorVersion"

                implementation(libs.logback)

                // transport models
                implementation(project(":ok-marketplace-api-v1-jackson"))
                implementation(project(":ok-marketplace-mappers-v1"))
                implementation(project(":ok-marketplace-lib-logging-logback"))

                // Это для логирования
                implementation("com.sndyuk:logback-more-appenders:1.8.8")
                implementation("org.fluentd:fluent-logger:0.3.4")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.ktor.test.host) // "io.ktor:ktor-server-test-host:$ktorVersion"
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.websockets)
            }
        }
    }
}

tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
