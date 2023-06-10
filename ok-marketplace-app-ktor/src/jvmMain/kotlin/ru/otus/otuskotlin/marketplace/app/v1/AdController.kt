package ru.otus.otuskotlin.marketplace.app.v1

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v1.models.*
import ru.otus.otuskotlin.marketplace.app.MkplAppSettings
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.mappers.v1.*

private val clazzCreate = ApplicationCall::createAd::class.qualifiedName ?: "create"
suspend fun ApplicationCall.createAd(appSettings: MkplAppSettings) {
    val logId = "create"
    val logger = appSettings.corSettings.loggerProvider.logger(clazzCreate)

    logger.doWithLogging(logId) {
        val processor = appSettings.processor
        val request = receive<AdCreateRequest>()
        val context = MkplContext()
        context.fromTransport(request)
        logger.info(
            msg = "${context.command} request is got",
            data = context.toLog("${logId}-request"),
        )
        processor.exec(context)
        respond(context.toTransportCreate())
        logger.info(
            msg = "${context.command} response is sent",
            data = context.toLog("${logId}-response")
        )
    }
}

suspend fun ApplicationCall.readAd(appSettings: MkplAppSettings) {
    val processor = appSettings.processor
    val request = receive<AdReadRequest>()
    val context = MkplContext()
    context.fromTransport(request)
    processor.exec(context)
    respond(context.toTransportRead())
}

suspend fun ApplicationCall.updateAd(appSettings: MkplAppSettings) {
    val processor = appSettings.processor
    val request = receive<AdUpdateRequest>()
    val context = MkplContext()
    context.fromTransport(request)
    processor.exec(context)
    respond(context.toTransportUpdate())
}

suspend fun ApplicationCall.deleteAd(appSettings: MkplAppSettings) {
    val processor = appSettings.processor
    val request = receive<AdDeleteRequest>()
    val context = MkplContext()
    context.fromTransport(request)
    processor.exec(context)
    respond(context.toTransportDelete())
}

suspend fun ApplicationCall.searchAd(appSettings: MkplAppSettings) {
    val processor = appSettings.processor
    val request = receive<AdSearchRequest>()
    val context = MkplContext()
    context.fromTransport(request)
    processor.exec(context)
    respond(context.toTransportSearch())
}