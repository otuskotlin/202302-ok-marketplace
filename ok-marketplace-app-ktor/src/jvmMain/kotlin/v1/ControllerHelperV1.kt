package ru.otus.otuskotlin.marketplace.app.v1

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.IResponse
import ru.otus.otuskotlin.marketplace.app.MkplAppSettings
import ru.otus.otuskotlin.marketplace.app.base.toModel
import ru.otus.otuskotlin.marketplace.biz.process
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd

suspend inline fun <reified Q : IRequest, @Suppress("unused") reified R : IResponse> ApplicationCall.processV1(
    appSettings: MkplAppSettings,
    logger: IMpLogWrapper,
    logId: String,
    command: MkplCommand,
) {
    appSettings.processor.process(logger, logId, command,
        {ctx ->
            val request = receive<Q>()
            ctx.principal = this@processV1.request.call.principal<JWTPrincipal>().toModel()
            ctx.fromTransport(request)
        },
        { ctx ->
            respond(ctx.toTransportAd())
        },
        { logId -> toLog(logId) })
}
