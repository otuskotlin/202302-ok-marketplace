package ru.otus.otuskotlin.marketplace.app.rabbit.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.koin.core.module.Module
import org.koin.dsl.module

import ru.otus.otuskotlin.marketplace.app.rabbit.controller.RabbitController
import ru.otus.otuskotlin.marketplace.app.rabbit.service.MessageService
import ru.otus.otuskotlin.marketplace.app.rabbit.service.RabbitConsumer


fun createKoinModule(rabbitHost: String, rabbitPort: Int, rabbitUser: String, rabbitPassword: String): Module {
    return module {
        val config = RabbitConfig(rabbitHost, rabbitPort,rabbitUser,rabbitPassword)
        single { MessageService() }
        single { RabbitController() }
        single { config }
        single { ObjectMapper() }
        single { RabbitConsumer(config.queueV1Channel) }
    }
}
