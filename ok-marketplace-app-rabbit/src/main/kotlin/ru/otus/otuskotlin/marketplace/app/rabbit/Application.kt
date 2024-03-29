package ru.otus.otuskotlin.marketplace.app.rabbit

import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.controller.RabbitController
import ru.otus.otuskotlin.marketplace.app.rabbit.processor.RabbitDirectProcessorV1
import ru.otus.otuskotlin.marketplace.app.rabbit.processor.RabbitDirectProcessorV2
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.logging.common.MpLoggerProvider
import ru.otus.otuskotlin.marketplace.logging.jvm.mpLoggerLogback


fun main() {
    val config = RabbitConfig(
        host = System.getenv("RABBIT_HOST") ?: RabbitConfig.HOST,
    )
    val corSettings = MkplCorSettings(
        loggerProvider = MpLoggerProvider { mpLoggerLogback(it) },
    )
    val adProcessor = MkplAdProcessor(corSettings)

    val producerConfigV1 = RabbitExchangeConfiguration(
        keyIn = "in-v1",
        keyOut = "out-v1",
        exchange = "transport-exchange-v1",
        queueIn = "v1-queue",
        queueOut= "v1-queue-out",
        consumerTag = "v1-consumer",
        exchangeType = "direct"
    )

    val producerConfigV2 = RabbitExchangeConfiguration(
        keyIn = "in-v2",
        keyOut = "out-v2",
        exchange = "transport-exchange-v2",
        queueIn = "v2-queue",
        queueOut = "v2-queue-out",
        consumerTag = "v2-consumer",
        exchangeType = "direct"
    )

    val processor by lazy {
        RabbitDirectProcessorV1(
            config = config,
            processorConfig = producerConfigV1,
            processor = adProcessor
        )
    }

    val processor2 by lazy {
        RabbitDirectProcessorV2(
            config = config,
            processorConfig = producerConfigV2,
            processor = adProcessor
        )
    }
    val controller by lazy {
        RabbitController(
            processors = setOf(processor, processor2)
        )
    }
    controller.start()
}
