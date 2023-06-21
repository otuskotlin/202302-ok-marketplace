package ru.otus.otuskotlin.marketplace.app.rabbit.processor

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import ru.otus.otuskotlin.marketplace.api.v2.apiV2RequestDeserialize
import ru.otus.otuskotlin.marketplace.api.v2.apiV2ResponseSerialize
import ru.otus.otuskotlin.marketplace.api.v2.models.IRequest
import ru.otus.otuskotlin.marketplace.app.rabbit.RabbitProcessorBase
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.config.corSettings
import ru.otus.otuskotlin.marketplace.app.rabbit.config.rabbitLogger
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v2.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v2.toTransportAd

class RabbitDirectProcessorV2(
    config: RabbitConfig,
    processorConfig: RabbitExchangeConfiguration,
    setting: MkplCorSettings = corSettings,
    private val processor: MkplAdProcessor = MkplAdProcessor(setting),
) : RabbitProcessorBase(config, processorConfig) {

    private val logger = setting.loggerProvider.logger(RabbitDirectProcessorV1::class)

    override suspend fun Channel.processMessage(message: Delivery) {
        processor.process(logger, "rabbit-v2", MkplCommand.NONE,
            { ctx ->
                apiV2RequestDeserialize<IRequest>(String(message.body)).also {
                    println("TYPE: ${it::class.java.simpleName}")
                    ctx.fromTransport(it)
                }
            },
            { ctx ->
                rabbitLogger.info("start publish")
                val response = ctx.toTransportAd()
                apiV2ResponseSerialize(response).also {
                    println("Publishing $response to ${processorConfig.exchange} exchange for keyOut ${processorConfig.keyOut}")
                    basicPublish(processorConfig.exchange, processorConfig.keyOut, null, it.toByteArray())
                }.also {
                    println("published")
                }
            })
    }
}
