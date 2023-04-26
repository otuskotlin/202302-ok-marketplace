package ru.otus.otuskotlin.marketplace.app.rabbit.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

var rabbitLogger: Logger = LoggerFactory.getLogger("RabbitLogger")
data class RabbitConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String
) {
}
