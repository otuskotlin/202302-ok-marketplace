package ru.otus.otuskotlin.marketplace.app.rabbit


import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.plugin.koin
import ru.otus.otuskotlin.marketplace.app.rabbit.config.RabbitConfig

const val QUEUE_V1 = "v1-queue"
 const val QUEUE_V2 = "v2-queue"
val config = RabbitConfig(
    host = "localhost",
    port = 5670,
    user = "rabbitmq",
    password = "rabbitmq",
    keyV1 = "key-v1",
    keyV2 = "key-v2",
    exchange = "transport-exchange",
    queueV1 = QUEUE_V1,
    queueV2 = QUEUE_V2,
    consumerTag = "v1-consumer",
    exchangeType = "direct"
)
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost") {
        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() {
    koin {
        modules(koinModule)
    }
    routing {
        val helloService: HelloService by inject(HelloService::class.java)
        get("/") {
            call.respondText("Hello World!")
        }
        get("/send-queue-v1") {
            call.respondText { "message sent"+helloService.sayHello() }
        }
    }
}
val koinModule = module {
    single { HelloService() }
    single { HelloRepository() }
}
class HelloService() {
    private val helloRepository: HelloRepository by inject(HelloRepository::class.java)
     fun sayHello() = "Hello ${helloRepository.getHello()} !"
}

class HelloRepository {
    fun getHello(): String = "Ktor & Koin"
}