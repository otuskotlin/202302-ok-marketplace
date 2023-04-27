package ru.otus.otuskotlin.marketplace.app.rabbit

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.testcontainers.containers.RabbitMQContainer
import ru.otus.otuskotlin.marketplace.api.v1.models.*
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitExchangeConfiguration
import ru.otus.otuskotlin.marketplace.app.rabbit.controller.RabbitController
import ru.otus.otuskotlin.marketplace.app.rabbit.processor.RabbitDirectProcessor
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RabbitMqTest {

    val container by lazy {
        RabbitMQContainer("rabbitmq:latest").apply {
            withExposedPorts(5672, 15672)
            withUser("rabbitmq", "rabbitmq")
            start()
        }
    }

    val config by lazy {
        RabbitConfig(
            port = container.getMappedPort(5672),
            host = container.host,
            user = container.adminUsername,
            password = container.adminPassword
        )
    }
    val processor by lazy {
        RabbitDirectProcessor(
            config = config,
            processorConfig = RabbitExchangeConfiguration(
                keyIn = "in-v1",
                keyOut = "out-v1",
                exchange = "test-exchange",
                queueIn = QUEUE_IN_NAME,
                queueOut = QUEUE_OUT_NAME,
                consumerTag = "test-tag",
                exchangeType = "direct"
            )
        )
    }
    val controller by lazy {
        RabbitController(
            processors = setOf(processor)
        )
    }
    val mapper = ObjectMapper()

    @BeforeTest
    fun tearUp() {
        controller.start()
    }

    @Test
    fun adCreateTest() {
        val keyOut = processor.processorConfig.keyOut
        val keyIn = processor.processorConfig.keyIn
        ConnectionFactory().apply {
            host = config.host
            port = config.port
            username = config.user
            password = config.password
        }.newConnection().use { connection ->
            connection.createChannel().use { channel ->
                var responseJson = ""
                channel.exchangeDeclare(processor.processorConfig.exchange, "direct")
                val queueOut = channel.queueDeclare().queue
                channel.queueBind(queueOut, processor.processorConfig.exchange, keyOut)
                val deliverCallback = DeliverCallback { consumerTag, delivery ->
                    responseJson = String(delivery.body, Charsets.UTF_8)
                    println(" [x] Received by $consumerTag: '$responseJson'")
                }
                channel.basicConsume(queueOut, true, deliverCallback, CancelCallback { })

                channel.basicPublish(processor.processorConfig.exchange, keyIn, null, mapper.writeValueAsBytes(boltCreateV1))

                Thread.sleep(10000)

                println("RESPONSE: $responseJson")
                val response = mapper.readValue(responseJson, AdCreateResponse::class.java)
                val expected = MkplAdStub.get()

                assertEquals(expected.title, response.ad?.title)
                assertEquals(expected.description, response.ad?.description)
            }
        }
    }
    private val boltCreateV1 = with(MkplAdStub.get()) {
        AdCreateRequest(
            ad = AdCreateObject(
                title = title,
                description = description
            ),
            requestType = "create",
            debug = AdDebug(
                mode = AdRequestDebugMode.STUB,
                stub = AdRequestDebugStubs.SUCCESS
            )
        )
    }
}
