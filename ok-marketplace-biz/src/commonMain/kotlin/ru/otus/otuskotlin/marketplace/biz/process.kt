package ru.otus.otuskotlin.marketplace.biz

import kotlinx.datetime.Clock
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.helpers.asMkplError
import ru.otus.otuskotlin.marketplace.common.helpers.fail
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper

suspend fun <T> MkplAdProcessor.process(
    logger: IMpLogWrapper,
    logId: String,
    command: MkplCommand,
    fromTransport: suspend (MkplContext) -> Unit,
    sendResponse: suspend (MkplContext) -> T,
    toLog: MkplContext.(logId: String) -> Any): T {

    val ctx = MkplContext(
        timeStart = Clock.System.now(),
    )
    var realCommand = command

    return try {
        logger.doWithLogging(id = logId) {
            fromTransport(ctx)
            realCommand = ctx.command

            logger.info(
                msg = "$realCommand request is got",
                data = ctx.toLog("${logId}-got")
            )
            exec(ctx)
            logger.info(
                msg = "$realCommand request is handled",
                data = ctx.toLog("${logId}-handled")
            )
            sendResponse(ctx)
        }
    } catch (e: Throwable) {
        logger.doWithLogging(id = "${logId}-failure") {
            logger.error(msg = "$realCommand handling failed")

            ctx.command = realCommand
            ctx.fail(e.asMkplError())
            exec(ctx)
            sendResponse(ctx)
        }
    }
}