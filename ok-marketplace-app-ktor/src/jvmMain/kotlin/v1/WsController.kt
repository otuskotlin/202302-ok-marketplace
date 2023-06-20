package ru.otus.otuskotlin.marketplace.app.v1

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.otus.otuskotlin.marketplace.api.v1.apiV1Mapper
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.app.MkplAppSettings
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.helpers.isUpdatableCommand
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportInit

class WsHandlerV1 {
    private val mutex = Mutex()
    private val sessions = mutableSetOf<WebSocketSession>()

    suspend fun handle(session: WebSocketSession, appSettings: MkplAppSettings) {
        mutex.withLock {
            sessions.add(session)
        }

        val logger = appSettings.corSettings.loggerProvider.logger(WsHandlerV1::class)

        // Handle init request
        val ctx = MkplContext()
        val init = apiV1Mapper.writeValueAsString(ctx.toTransportInit())
        session.outgoing.send(Frame.Text(init))

        // Handle flow
        session.incoming.receiveAsFlow().mapNotNull { it ->
            val frame = it as? Frame.Text ?: return@mapNotNull

            val jsonStr = frame.readText()

            // Handle without flow destruction
            appSettings.processor.process(logger, "webSocket", MkplCommand.NONE,
                { ctx ->
                    val request = apiV1Mapper.readValue<IRequest>(jsonStr)
                    ctx.fromTransport(request)
                },
                { ctx ->
                    try {
                        val result = apiV1Mapper.writeValueAsString(ctx.toTransportAd())

                        // If change request, response is sent to everyone
                        if (ctx.isUpdatableCommand()) {
                            mutex.withLock {
                                sessions.forEach {
                                    it.send(Frame.Text(result))
                                }
                            }
                        } else {
                            session.outgoing.send(Frame.Text(result))
                        }
                    } catch (_: ClosedReceiveChannelException) {
                        mutex.withLock {
                            sessions.clear()
                        }
                    }
                })
        }.collect()
    }
}
