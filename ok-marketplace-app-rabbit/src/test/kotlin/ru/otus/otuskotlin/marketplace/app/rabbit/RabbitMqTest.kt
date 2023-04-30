package ru.otus.otuskotlin.marketplace.app.rabbit


import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.koin.ktor.plugin.koin
import org.testcontainers.containers.RabbitMQContainer
import ru.otus.otuskotlin.marketplace.api.v1.models.*
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RABBIT_PASSWORD
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RABBIT_USER
import ru.otus.otuskotlin.marketplace.app.rabbit.config.configureRouting
import ru.otus.otuskotlin.marketplace.app.rabbit.config.createKoinModule
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.Test
import kotlin.test.assertEquals

class RabbitMqTest {
    val jacksonObjectMapper: ObjectMapper = ObjectMapper()
    val container by lazy {
        RabbitMQContainer("rabbitmq:latest").apply {
            withExposedPorts(5672, 15672)
            withUser(RABBIT_USER, RABBIT_PASSWORD)
            start()
        }
    }
    lateinit var testClient1:HttpClient

    @Test
    fun adCreateTest() {
        testApplication {
            testClient1=client
            application {
                configureRouting()
                koin {
                    modules(createKoinModule(
                        container.host,
                        container.getMappedPort(5672),
                        container.adminUsername,
                        container.adminPassword
                    ))
                }
            }

            val response = testClient1.post("/advertisement") {
                setBody(jacksonObjectMapper.writeValueAsString(boltCreateV1))
            }
            assertEquals(HttpStatusCode.Created, response.status)
            Thread.sleep(5000)
            val httpResponse = testClient1.get("/advertisement") {
                parameter("request_id", response.bodyAsText())
            }
            val adCreateResponse = jacksonObjectMapper.readValue(httpResponse.bodyAsText(), AdCreateResponse::class.java)
            assertEquals(response.bodyAsText(), adCreateResponse.requestId)
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

