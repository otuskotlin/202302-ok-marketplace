package ru.otus.otuskotlin.marketplace.app.rabbit.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import kotlinx.datetime.Clock
import org.koin.java.KoinJavaComponent
import ru.otus.otuskotlin.marketplace.api.v1.models.AdCreateRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.IResponse
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RABBIT_CONSUMER_TAG
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RABBIT_QUEUE_V1
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.rabbitLogger
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd
import java.nio.charset.StandardCharsets
import java.util.*

class MessageService {
    private val rabbitConfig: RabbitConfig by KoinJavaComponent.inject(RabbitConfig::class.java)
    private val mapper: ObjectMapper by KoinJavaComponent.inject(ObjectMapper::class.java)
    private val messageStorage:HashMap<String,IResponse> = hashMapOf()

    init {
        rabbitLogger.info("message service init")
        val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            val context = MkplContext(timeStart = Clock.System.now())
            context.fromTransport(mapper.readValue(String(delivery.body), IRequest::class.java))
            val iResponse = context.toTransportAd()
            messageStorage[iResponse.requestId!!] = iResponse
            println("[$consumerTag] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { consumerTag: String? ->
            println("[$consumerTag] was cancelled")
        }

        rabbitConfig.queueV1Channel.basicConsume(RABBIT_QUEUE_V1, true, RABBIT_CONSUMER_TAG, deliverCallback, cancelCallback)
    }

    fun sendMessage(message: AdCreateRequest): String {
        val requestId = UUID.randomUUID().toString()
        val request = message.copy(
            requestId = requestId
        )
        rabbitConfig.publishQueueV1(mapper.writeValueAsString(request))
        return requestId
    }
    fun getResponse(requestId:String): IResponse {
        return messageStorage[requestId]?:throw Exception("no such response")
    }
    }
