package ru.otus.otuskotlin.marketplace.biz

import kotlinx.datetime.Clock
import ru.otus.otuskotlin.marketplace.api.logs.mapper.toLog
import ru.otus.otuskotlin.marketplace.biz.general.initRepo
import ru.otus.otuskotlin.marketplace.biz.general.prepareResult
import ru.otus.otuskotlin.marketplace.biz.groups.operation
import ru.otus.otuskotlin.marketplace.biz.groups.stubs
import ru.otus.otuskotlin.marketplace.biz.repo.*
import ru.otus.otuskotlin.marketplace.biz.validation.*
import ru.otus.otuskotlin.marketplace.biz.workers.*
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.helpers.asMkplError
import ru.otus.otuskotlin.marketplace.common.helpers.fail
import ru.otus.otuskotlin.marketplace.common.models.MkplAdId
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.common.models.MkplState
import ru.otus.otuskotlin.marketplace.cor.chain
import ru.otus.otuskotlin.marketplace.cor.rootChain
import ru.otus.otuskotlin.marketplace.cor.worker
import ru.otus.otuskotlin.marketplace.logging.common.IMpLogWrapper


class MkplAdProcessor(
    private val settings: MkplCorSettings = MkplCorSettings()
) {
    suspend fun exec(ctx: MkplContext) = BusinessChain.exec(ctx.apply { settings =  this@MkplAdProcessor.settings})

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

    companion object {
        private val BusinessChain = rootChain<MkplContext> {
            initStatus("Инициализация статуса")
            initRepo("Инициализация репозитория")
            operation("Создание объявления", MkplCommand.CREATE) {
                stubs("Обработка стабов") {
                    stubCreateSuccess("Имитация успешной обработки")
                    stubValidationBadTitle("Имитация ошибки валидации заголовка")
                    stubValidationBadDescription("Имитация ошибки валидации описания")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
                validation {
                    worker("Копируем поля в adValidating") { adValidating = adRequest.deepCopy() }
                    worker("Очистка id") { adValidating.id = MkplAdId.NONE }
                    worker("Очистка заголовка") { adValidating.title = adValidating.title.trim() }
                    worker("Очистка описания") { adValidating.description = adValidating.description.trim() }
                    validateTitleNotEmpty("Проверка, что заголовок не пуст")
                    validateTitleHasContent("Проверка символов")
                    validateDescriptionNotEmpty("Проверка, что описание не пусто")
                    validateDescriptionHasContent("Проверка символов")

                    finishAdValidation("Завершение проверок")
                }
                chain {
                    title = "Логика сохранения"
                    repoPrepareCreate("Подготовка объекта для сохранения")
                    repoCreate("Создание объявления в БД")
                }
                prepareResult("Подготовка ответа")
            }
            operation("Получить объявление", MkplCommand.READ) {
                stubs("Обработка стабов") {
                    stubReadSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
                validation {
                    worker("Копируем поля в adValidating") { adValidating = adRequest.deepCopy() }
                    worker("Очистка id") { adValidating.id = MkplAdId(adValidating.id.asString().trim()) }
                    validateIdNotEmpty("Проверка на непустой id")
                    validateIdProperFormat("Проверка формата id")

                    finishAdValidation("Успешное завершение процедуры валидации")
                }

                chain {
                    title = "Логика чтения"
                    repoRead("Чтение объявления из БД")
                    worker {
                        title = "Подготовка ответа для Read"
                        on { state == MkplState.RUNNING }
                        handle { adRepoDone = adRepoRead }
                    }
                }
                prepareResult("Подготовка ответа")
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
                validation {
                    worker("Копируем поля в adValidating") { adValidating = adRequest.deepCopy() }
                    worker("Очистка id") { adValidating.id = MkplAdId(adValidating.id.asString().trim()) }
                    worker("Очистка заголовка") { adValidating.title = adValidating.title.trim() }
                    worker("Очистка описания") { adValidating.description = adValidating.description.trim() }
                    validateIdNotEmpty("Проверка на непустой id")
                    validateIdProperFormat("Проверка формата id")
                    validateTitleNotEmpty("Проверка на непустой заголовок")
                    validateTitleHasContent("Проверка на наличие содержания в заголовке")
                    validateDescriptionNotEmpty("Проверка на непустое описание")
                    validateDescriptionHasContent("Проверка на наличие содержания в описании")

                    finishAdValidation("Успешное завершение процедуры валидации")
                }
                chain {
                    title = "Логика сохранения"
                    repoRead("Чтение объявления из БД")
                    repoPrepareUpdate("Подготовка объекта для обновления")
                    repoUpdate("Обновление объявления в БД")
                }
                prepareResult("Подготовка ответа")
            }
            operation("Удалить объявление", MkplCommand.DELETE) {
                stubs("Обработка стабов") {
                    stubDeleteSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
                validation {
                    worker("Копируем поля в adValidating") {
                        adValidating = adRequest.deepCopy() }
                    worker("Очистка id") { adValidating.id = MkplAdId(adValidating.id.asString().trim()) }
                    validateIdNotEmpty("Проверка на непустой id")
                    validateIdProperFormat("Проверка формата id")
                    finishAdValidation("Успешное завершение процедуры валидации")
                }
                chain {
                    title = "Логика удаления"
                    repoRead("Чтение объявления из БД")
                    repoPrepareDelete("Подготовка объекта для удаления")
                    repoDelete("Удаление объявления из БД")
                }
                prepareResult("Подготовка ответа")
            }
            operation("Поиск объявлений", MkplCommand.SEARCH) {
                stubs("Обработка стабов") {
                    stubSearchSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
                validation {
                    worker("Копируем поля в adFilterValidating") { adFilterValidating = adFilterRequest.copy() }

                    finishAdFilterValidation("Успешное завершение процедуры валидации")
                }
                repoSearch("Поиск объявления в БД по фильтру")
                prepareResult("Подготовка ответа")
            }
            operation("Поиск подходящих предложений для объявления", MkplCommand.OFFERS) {
                stubs("Обработка стабов") {
                    stubOffersSuccess("Имитация успешной обработки")
                    stubValidationBadId("Имитация ошибки валидации id")
                    stubDbError("Имитация ошибки работы с БД")
                    stubNoCase("Ошибка: запрошенный стаб недопустим")
                }
                validation {
                    worker("Копируем поля в adValidating") { adValidating = adRequest.deepCopy() }
                    worker("Очистка id") { adValidating.id = MkplAdId(adValidating.id.asString().trim()) }
                    validateIdNotEmpty("Проверка на непустой id")
                    validateIdProperFormat("Проверка формата id")

                    finishAdValidation("Успешное завершение процедуры валидации")
                }
                chain {
                    title = "Логика поиска в БД"
                    repoRead("Чтение объявления из БД")
                    repoPrepareOffers("Подготовка данных для поиска предложений")
                    repoOffers("Поиск предложений для объявления в БД")
                }
                prepareResult("Подготовка ответа")
            }
        }.build()
    }
}
