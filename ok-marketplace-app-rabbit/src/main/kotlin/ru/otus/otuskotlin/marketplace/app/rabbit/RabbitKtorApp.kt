package ru.otus.otuskotlin.marketplace.app.rabbit


import io.ktor.server.application.Application
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.koin
import ru.otus.otuskotlin.marketplace.app.rabbit.config.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myApplicationModule).start(wait = true)
}

fun Application.myApplicationModule() {
    koin {
        modules(createKoinModule(RABBIT_HOST, RABBIT_PORT, RABBIT_USER, RABBIT_PASSWORD))
    }
    configureRouting()
}



