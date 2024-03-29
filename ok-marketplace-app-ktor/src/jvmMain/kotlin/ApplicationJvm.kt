package ru.otus.otuskotlin.marketplace.app

import com.auth0.jwt.JWT
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import ru.otus.otuskotlin.marketplace.api.v1.apiV1Mapper
import ru.otus.otuskotlin.marketplace.app.base.KtorAuthConfig.Companion.GROUPS_CLAIM
import ru.otus.otuskotlin.marketplace.app.base.resolveAlgorithm
import ru.otus.otuskotlin.marketplace.app.plugins.initAppSettings
import ru.otus.otuskotlin.marketplace.app.v1.WsHandlerV1
import ru.otus.otuskotlin.marketplace.app.v1.v1Ad
import ru.otus.otuskotlin.marketplace.app.v1.v1Offer
import ru.otus.otuskotlin.marketplace.app.v2.WsHandlerV2
import ru.otus.otuskotlin.marketplace.app.module as commonModule

// function with config (application.conf)
fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)


@Suppress("unused") // Referenced in application.conf
fun Application.moduleJvm(appSettings: MkplAppSettings = initAppSettings()) {
    commonModule(appSettings)
    val wsHandlerV1 = WsHandlerV1()
    val wsHandlerV2 = WsHandlerV2()

    install(Authentication) {
        jwt("auth-jwt") {
            val authConfig = appSettings.auth
            realm = authConfig.realm

            verifier {
                val algorithm = it.resolveAlgorithm(authConfig)
                JWT
                    .require(algorithm)
                    .withAudience(authConfig.audience)
                    .withIssuer(authConfig.issuer)
                    .build()
            }
            validate { jwtCredential: JWTCredential ->
                when {
                    jwtCredential.payload.getClaim(GROUPS_CLAIM).asList(String::class.java).isNullOrEmpty() -> {
                        this@moduleJvm.log.error("Groups claim must not be empty in JWT token")
                        null
                    }

                    else -> JWTPrincipal(jwtCredential.payload)
                }
            }
        }
    }
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }

        route("v1") {
            pluginRegistry.getOrNull(AttributeKey("ContentNegotiation"))?: install(ContentNegotiation) {
                jackson {
                    setConfig(apiV1Mapper.serializationConfig)
                    setConfig(apiV1Mapper.deserializationConfig)
                }
            }

            authenticate("auth-jwt") {
                v1Ad(appSettings)
                v1Offer(appSettings)
            }
        }

        webSocket("/ws/v1") {
            wsHandlerV1.handle(this, appSettings)
        }
        webSocket("/ws/v2") {
            wsHandlerV2.handle(this, appSettings)
        }

        static("static") {
            resources("static")
        }
    }
}
