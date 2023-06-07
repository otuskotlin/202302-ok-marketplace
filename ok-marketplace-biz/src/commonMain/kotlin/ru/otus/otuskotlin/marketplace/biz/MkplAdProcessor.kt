package ru.otus.otuskotlin.marketplace.biz

import kotlinx.datetime.Clock
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.biz.groups.operation
import ru.otus.otuskotlin.marketplace.biz.groups.stubs
import ru.otus.otuskotlin.marketplace.biz.workers.*
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.cor.rootChain
import ru.otus.otuskotlin.marketplace.cor.worker
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper

class MkplAdProcessor() {
    suspend fun exec(ctx: MkplContext) = BusinessChain.exec(ctx)

    suspend fun <T> process(
        logger: IMpLogWrapper,
        logId: String,
        command: MkplCommand,
        fromTransport: suspend (MkplContext) -> Unit,
        sendResponse: suspend (MkplContext) -> T): T {

        val ctx = MkplContext(
            timeStart = Clock.System.now(),
        )
        var realCommand = command

        return logger.doWithLogging(id = logId) {
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
    }

    companion object {
        private val BusinessChain = rootChain<MkplContext> {
            initStatus("Инициализация статуса")

            operation("Создание объявления", MkplCommand.CREATE) {
                stubs("Обработка стабов") {
                    stubCreateSuccess("Имитация успешной обработки")
                    stubValidationBadTitle("Имитация ошибки валидации заголовка")
                    stubValidationBadDescription("Имитация ошибки валидации описания")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
            }
            operation("Получить объявление", MkplCommand.READ) {
                stubs("Обработка стабов") {
                    stubReadSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
            }
            operation("Изменить объявление", MkplCommand.UPDATE) {
                stubs("Обработка стабов") {
                    stubUpdateSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubValidationBadTitle("Имитация ошибки валидации заголовка")
                    stubValidationBadDescription("Имитация ошибки валидации описания")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
            }
            operation("Удалить объявление", MkplCommand.DELETE) {
                stubs("Обработка стабов") {
                    stubDeleteSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
            }
            operation("Поиск объявлений", MkplCommand.SEARCH) {
                stubs("Обработка стабов") {
                    stubSearchSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }

            }
            operation("Поиск подходящих предложений для объявления", MkplCommand.OFFERS) {
                stubs("Обработка стабов") {
                    stubOffersSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
            }
        }.build()
    }
}
