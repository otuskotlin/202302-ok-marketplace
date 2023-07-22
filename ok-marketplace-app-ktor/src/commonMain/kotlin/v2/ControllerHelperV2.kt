package ru.otus.otuskotlin.marketplace.app.v2

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v2.apiV2Mapper
import ru.otus.otuskotlin.marketplace.api.v2.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.IResponse
import ru.otus.otuskotlin.marketplace.app.MkplAppSettings
import ru.otus.otuskotlin.marketplace.biz.process
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.common.models.MkplUserId
import ru.otus.otuskotlin.marketplace.common.permissions.MkplPrincipalModel
import ru.otus.otuskotlin.marketplace.common.permissions.MkplUserGroups
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper
import ru.otus.otuskotlin.marketplace.mappers.v2.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v2.toTransportAd

suspend inline fun <reified Q : IRequest, @Suppress("unused") reified R : IResponse> ApplicationCall.processV2(
    appSettings: MkplAppSettings,
    logger: IMpLogWrapper,
    logId: String,
    command: MkplCommand,
) {
    appSettings.processor.process(
        logger = logger,
        logId = logId,
        command = command,
        fromTransport = { ctx ->
            val request = apiV2Mapper.decodeFromString<Q>(receiveText())
            ctx.principal = mkplPrincipal(appSettings)
            ctx.fromTransport(request)
        },
        sendResponse = { ctx ->
            respond(apiV2Mapper.encodeToString(ctx.toTransportAd()))
        },
        toLog = { logId -> toLog(logId) }
    )
}

// TODO: костыль для решения проблемы отсутствия jwt в native
@Suppress("UnusedReceiverParameter", "UNUSED_PARAMETER")
fun ApplicationCall.mkplPrincipal(appSettings: MkplAppSettings): MkplPrincipalModel = MkplPrincipalModel(
    id = MkplUserId("user-1"),
    fname = "Ivan",
    mname = "Ivanovich",
    lname = "Ivanov",
    groups = setOf(MkplUserGroups.TEST, MkplUserGroups.USER),
)
