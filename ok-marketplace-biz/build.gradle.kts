plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
        withJava()
    }
    linuxX64 {}
    macosX64 {}
    macosArm64 {}

    sourceSets {
        val coroutinesVersion: String by project

        all { languageSettings.optIn("kotlin.RequiresOptIn") }

        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":ok-marketplace-common"))
                implementation(project(":ok-marketplace-auth"))
                implementation(project(":ok-marketplace-repo-in-memory"))
                implementation(project(":ok-marketplace-stubs"))
                implementation(project(":ok-marketplace-lib-cor"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                api("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

                implementation(project(":ok-marketplace-repo-tests"))
                implementation(project(":ok-marketplace-repo-stubs"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}
