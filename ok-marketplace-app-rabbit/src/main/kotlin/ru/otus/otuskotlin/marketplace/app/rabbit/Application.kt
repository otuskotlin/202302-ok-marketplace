package ru.otus.otuskotlin.marketplace.app.rabbit

import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.config.rabbitLogger
import ru.otus.otuskotlin.marketplace.app.rabbit.controller.RabbitController
import ru.otus.otuskotlin.marketplace.app.rabbit.processor.RabbitDirectProcessor
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor

 const val QUEUE_IN_NAME = "v1-queue-in"
 const val QUEUE_OUT_NAME = "v1-queue-out"

fun main() {

    val config = RabbitConfig(
        host = System.getenv("RABBIT_HOST") ?: "localhost",
        port = 5672,
        user = "rabbitmq",
        password = "rabbitmq"
    )
    val adProcessor = MkplAdProcessor()

    val producerConfig = RabbitExchangeConfiguration(
        keyIn = "in-v1",
        keyOut = "out-v1",
        exchange = "transport-exchange",
        queueIn = QUEUE_IN_NAME,
        queueOut = QUEUE_OUT_NAME,
        consumerTag = "v1-consumer",
        exchangeType = "direct"
    )

    val processor by lazy {
        RabbitDirectProcessor(
            config = config,
            processorConfig = producerConfig,
            processor = adProcessor
        )
    }
    val controller by lazy {
        RabbitController(
            processors = setOf(processor)
        )
    }
    rabbitLogger.info("rabbit processor started")

    controller.start()
}
