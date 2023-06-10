package ru.otus.otuskotlin.marketplace.app

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.locations.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import org.slf4j.event.Level
import ru.otus.otuskotlin.marketplace.api.v1.apiV1Mapper
import ru.otus.otuskotlin.marketplace.app.plugins.initAppSettings
import ru.otus.otuskotlin.marketplace.app.v1.WsHandlerV1
import ru.otus.otuskotlin.marketplace.app.v1.v1Ad
import ru.otus.otuskotlin.marketplace.app.v1.v1Offer
import ru.otus.otuskotlin.marketplace.app.v2.WsHandlerV2
import ru.otus.otuskotlin.marketplace.logging.jvm.MpLogWrapperLogback
import ru.otus.otuskotlin.marketplace.app.module as commonModule

// function with config (application.conf)
fun main(args: Array<String>): Unit = io.ktor.server.cio.EngineMain.main(args)


@Suppress("unused") // Referenced in application.conf
fun Application.moduleJvm(appSettings: MkplAppSettings = initAppSettings()) {
    commonModule(appSettings)
    val wsHandlerV1 = WsHandlerV1()
    val wsHandlerV2 = WsHandlerV2()

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

            v1Ad(appSettings)
            v1Offer(appSettings)
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
