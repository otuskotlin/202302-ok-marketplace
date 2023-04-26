package ru.otus.otuskotlin.marketplace.app.rabbit.processor

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Delivery
import kotlinx.datetime.Clock
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.app.rabbit.RabbitProcessorBase
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.config.rabbitLogger
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.helpers.addError
import ru.otus.otuskotlin.marketplace.common.helpers.asMkplError
import ru.otus.otuskotlin.marketplace.common.models.MkplState
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd

class RabbitDirectProcessor(
    config: RabbitConfig,
    processorConfig: RabbitExchangeConfiguration,
    private val processor: MkplAdProcessor = MkplAdProcessor(),
) : RabbitProcessorBase(config, processorConfig) {
    override suspend fun Channel.processMessage(message: Delivery) {
        val context = MkplContext(timeStart = Clock.System.now())
        context.fromTransport(jacksonMapper.readValue(String(message.body), IRequest::class.java))
        val response = processor.exec(context).run { context.toTransportAd() }
        basicPublish(processorConfig.exchange, processorConfig.keyOut, null,jacksonMapper.writeValueAsBytes(response))
        rabbitLogger.info("message was processed $response")
    }

    override fun Channel.onError(e: Throwable) {
        val context = MkplContext(timeStart = Clock.System.now())
        context.state = MkplState.FAILING
        context.addError(error = arrayOf(e.asMkplError()))
        val response = context.toTransportAd()
        jacksonMapper.writeValueAsBytes(response).also {
            basicPublish(processorConfig.exchange, processorConfig.keyOut, null, it)
        }
    }
}
