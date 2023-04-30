val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val rabbitVersion: String by project
val jacksonVersion: String by project
val coroutinesVersion: String by project
val testContainersVersion: String by project
val koinVersion: String by project
plugins {
    kotlin("jvm")
    id("com.bmuschko.docker-remote-api")
}
apply(plugin = "com.bmuschko.docker-remote-api")

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.rabbitmq:amqp-client:$rabbitVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    //ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    //koin
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    // transport models common
    implementation(project(":ok-marketplace-common"))

    // v1 api
    implementation(project(":ok-marketplace-api-v1-jackson"))
    implementation(project(":ok-marketplace-mappers-v1"))

    // v2 api
    implementation(project(":ok-marketplace-mappers-v2"))
    implementation(project(":ok-marketplace-api-v2-kmp"))

    // Services
    implementation(project(":ok-marketplace-biz"))

    // Stubs
    implementation(project(":ok-marketplace-stubs"))

    testImplementation("org.testcontainers:rabbitmq:$testContainersVersion")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation ("io.insert-koin:koin-test:$koinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}
