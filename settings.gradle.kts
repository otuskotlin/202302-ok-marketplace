rootProject.name = "ok-marketplace-202302"

pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
    }
}

include("m1l1-hello")
include("m1l2-basic")
include("m1l3-oop")
include("m1l4-dsl")
include("m1l5-coroutines")
include("m1l6-flows-and-channels")
include("m1l7-kmp")
