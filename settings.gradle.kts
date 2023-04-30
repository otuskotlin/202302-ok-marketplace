rootProject.name = "ok-marketplace-202302"

pluginManagement {
    val kotlinVersion: String by settings
    val openapiVersion: String by settings
    val springframeworkBootVersion:String by settings
    val springDependencyManagementVersion:String by settings
    val pluginSpringVersion:String by settings
    val pluginJpa:String by settings
    val ktorVersion:String by settings
    val bmuschkoVersion:String by settings
    plugins {
        kotlin("jvm") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false

        id("org.springframework.boot") version springframeworkBootVersion apply false
        id("io.spring.dependency-management") version springDependencyManagementVersion apply false
        kotlin("plugin.spring") version pluginSpringVersion apply false
        kotlin("plugin.jpa") version pluginJpa apply false

        id("io.ktor.plugin") version ktorVersion apply false

        id("org.openapi.generator") version openapiVersion apply false

        id("com.bmuschko.docker-java-application") version bmuschkoVersion apply false
        id("com.bmuschko.docker-spring-boot-application") version bmuschkoVersion apply false
        id("com.bmuschko.docker-remote-api") version bmuschkoVersion apply false
    }
}

include("ok-mkpl-acceptance")
include("ok-mkpl-acceptance-rest")

include("ok-marketplace-api-v1-jackson")
include("ok-marketplace-api-v2-kmp")

include("ok-marketplace-common")
include("ok-marketplace-mappers-v1")
include("ok-marketplace-mappers-v2")

include("ok-marketplace-biz")
include("ok-marketplace-stubs")
include("ok-marketplace-app-rabbit")

include("ok-marketplace-app-spring")
include("ok-marketplace-app-ktor")
