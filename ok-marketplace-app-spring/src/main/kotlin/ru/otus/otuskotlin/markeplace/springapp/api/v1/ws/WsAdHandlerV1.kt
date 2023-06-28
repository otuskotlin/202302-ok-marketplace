package ru.otus.otuskotlin.markeplace.springapp.api.v1.ws

import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v1.apiV1Mapper
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.biz.process
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.helpers.isUpdatableCommand
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportInit

@Component
class WsAdHandlerV1(
    private val processor: MkplAdProcessor,
    settings: MkplCorSettings
) : TextWebSocketHandler() {
    private val sessions = mutableMapOf<String, WebSocketSession>()
    private val logger = settings.loggerProvider.logger(WsAdHandlerV1::class)

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session

        val context = MkplContext()

        val msg = apiV1Mapper.writeValueAsString(context.toTransportInit())
        session.sendMessage(TextMessage(msg))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        runBlocking {
            processor.process(logger, "ws-v1", MkplCommand.NONE,
                { ctx ->
                    val request = apiV1Mapper.readValue(message.payload, IRequest::class.java)
                    ctx.fromTransport(request)
                },
                { ctx ->
                    val result = apiV1Mapper.writeValueAsString(ctx.toTransportAd())
                    if (ctx.isUpdatableCommand()) {
                        sessions.values.forEach {
                            it.sendMessage(TextMessage(result))
                        }
                    } else {
                        session.sendMessage(TextMessage(result))
                    }
                },
                { logId -> toLog(logId) })
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session.id)
    }
}
