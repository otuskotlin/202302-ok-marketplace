package ru.otus.otuskotlin.marketplace.app.rabbit.service

import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import org.koin.java.KoinJavaComponent
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RABBIT_QUEUE_V1
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig

class RabbitConsumer(
    channel: Channel

) : DefaultConsumer(channel) {
    private val rabbitConfig: RabbitConfig by KoinJavaComponent.inject(RabbitConfig::class.java)
    init {

        rabbitConfig.queueV1Channel.basicConsume(RABBIT_QUEUE_V1, true, this)
    }
}