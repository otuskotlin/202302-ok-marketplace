plugins {
    alias(libs.plugins.kotlin.jvm)
}

val coroutinesVersion: String by project
val jUnitJupiterVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$jUnitJupiterVersion")
}

tasks.test {
    useJUnitPlatform()
}
