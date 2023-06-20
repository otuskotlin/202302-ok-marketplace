package ru.otus.otuskotlin.marketplace.app.rabbit.processor

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import ru.otus.otuskotlin.marketplace.api.v1.apiV1Mapper
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.app.rabbit.RabbitProcessorBase
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.config.corSettings
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd

// TODO-rmq-6: наследник RabbitProcessorBase, увязывает транспортную и бизнес-части
class RabbitDirectProcessorV1(
    config: RabbitConfig,
    processorConfig: RabbitExchangeConfiguration,
    setting: MkplCorSettings = corSettings,
    private val processor: MkplAdProcessor = MkplAdProcessor(setting),
) : RabbitProcessorBase(config, processorConfig) {

    private val logger = setting.loggerProvider.logger(RabbitDirectProcessorV1::class)

    override suspend fun Channel.processMessage(message: Delivery) {
        processor.process(logger, "rabbit-v1", MkplCommand.NONE,
            { ctx ->
                apiV1Mapper.readValue(message.body, IRequest::class.java).run {
                    ctx.fromTransport(this).also {
                        println("TYPE: ${this::class.simpleName}")
                    }
                }
            },
            { ctx ->
                val response = ctx.toTransportAd()
                apiV1Mapper.writeValueAsBytes(response).also {
                    println("Publishing $response to ${processorConfig.exchange} exchange for keyOut ${processorConfig.keyOut}")
                    basicPublish(processorConfig.exchange, processorConfig.keyOut, null, it)
                }.also {
                    println("published")
                }
            })
    }
}
