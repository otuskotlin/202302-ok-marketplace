package ru.otus.otuskotlin.markeplace.springapp.api.v1.controller

import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.IResponse
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.biz.process
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper
import ru.otus.otuskotlin.marketplace.mappers.v1.fromTransport
import ru.otus.otuskotlin.marketplace.mappers.v1.toTransportAd

suspend inline fun <reified Q : IRequest, reified R : IResponse> processV1(
    processor: MkplAdProcessor,
    command: MkplCommand,
    request: Q,
    logger: IMpLogWrapper,
    logId: String,
): R  = processor.process(logger, logId, command,
        { ctx -> ctx.fromTransport(request) },
        { ctx -> ctx.toTransportAd() as R },
        { logId -> toLog(logId) }
)
