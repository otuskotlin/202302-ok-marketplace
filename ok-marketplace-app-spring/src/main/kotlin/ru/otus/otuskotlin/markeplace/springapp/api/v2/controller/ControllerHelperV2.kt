package ru.otus.otuskotlin.markeplace.springapp.api.v2.controller

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v2.apiV2Mapper
import ru.otus.otuskotlin.marketplace.api.v2.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.IResponse
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.biz.process
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper
import ru.otus.otuskotlin.marketplace.mappers.v2.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v2.toTransportAd

suspend inline fun <reified Q : IRequest, @Suppress("unused") reified R : IResponse> processV2(
    processor: MkplAdProcessor,
    command: MkplCommand,
    requestString: String,
    logger: IMpLogWrapper,
    logId: String,
): String = processor.process(logger, logId, command,
        { ctx ->
            val request = apiV2Mapper.decodeFromString<Q>(requestString)
            ctx.fromTransport(request)
        },
        { ctx -> apiV2Mapper.encodeToString(ctx.toTransportAd()) },
        { logId -> toLog(logId) })
