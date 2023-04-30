package ru.otus.otuskotlin.marketplace.app.rabbit.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.koin.java.KoinJavaComponent.inject
import ru.otus.otuskotlin.marketplace.api.v1.models.AdCreateRequest
import ru.otus.otuskotlin.marketplace.app.rabbit.service.MessageService

class RabbitController {

    private val helloService: MessageService by inject(MessageService::class.java)
    private val mapper: ObjectMapper by inject(ObjectMapper::class.java)
    fun sendQueue(message: String): String {
        return helloService.sendMessage(mapper.readValue(message,AdCreateRequest::class.java))
    }
    fun getResponse(message: String): String {
        return mapper.writeValueAsString(helloService.getResponse(message))
    }
}


