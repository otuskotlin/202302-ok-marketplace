package ru.otus.otuskotlin.marketplace.app.rabbit.config

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

var rabbitLogger: Logger = LoggerFactory.getLogger("RabbitLogger")

const val RABBIT_QUEUE_V1 = "v1-queue"
const val RABBIT_HOST = "localhost"
const val RABBIT_PORT = 5670
const val RABBIT_USER = "rabbitmq"
const val RABBIT_PASSWORD = "rabbitmq"
const val RABBIT_KEY_V1 = "key-v1"
const val RABBIT_KEY_V2 = "key-v2"
const val RABBIT_EXCHANGE = "transport-exchange"
const val RABBIT_CONSUMER_TAG = "v1-consumer"

class RabbitConfig(rabbitHost: String, rabbitPort: Int, rabbitUser: String, rabbitPassword: String) {
    val factory = ConnectionFactory().apply {
        host = rabbitHost
        port = rabbitPort
        username = rabbitUser
        password = rabbitPassword
    }
    private val connection = factory.newConnection()

    val queueV1Channel = connection.createChannel().apply {
        exchangeDeclare(RABBIT_EXCHANGE, "direct")
        queueDeclare(RABBIT_QUEUE_V1,true,false,true,null)
        queueBind(RABBIT_QUEUE_V1,RABBIT_EXCHANGE, RABBIT_KEY_V1)
    }
    fun publishQueueV1(message:String){
        queueV1Channel.basicPublish(RABBIT_EXCHANGE, RABBIT_KEY_V1,null,message.toByteArray(charset("UTF-8")))
    }

}
