package ru.otus.otuskotlin.marketplace.app.rabbit.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent
import ru.otus.otuskotlin.marketplace.app.rabbit.controller.RabbitController

fun Application.configureRouting() {

    routing {
        val controller: RabbitController by KoinJavaComponent.inject(RabbitController::class.java)
        get("/advertisement") {
            val id = call.parameters["request_id"]?:throw Exception("request_id was null")
            call.respondText(status = HttpStatusCode.OK) { controller.getResponse(id)}
        }
        post("/advertisement") {
            val adCreateRequest = call.receive<String>()
            call.respondText(status = HttpStatusCode.Created) { controller.sendQueue(adCreateRequest)}
        }
    }
}