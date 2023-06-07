package ru.otus.otuskotlin.markeplace.springapp.api.v1.controller

import org.springframework.web.bind.annotation.*
import ru.otus.otuskotlin.marketplace.api.v1.models.*
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v1.*

@RestController
@RequestMapping("v1/ad")
class AdController(
    private val processor: MkplAdProcessor,
    settings: MkplCorSettings) {

    private val logger = settings.loggerProvider.logger(AdController::class)

    @PostMapping("create")
    suspend fun createAd(@RequestBody request: AdCreateRequest): AdCreateResponse =
        processV1(processor, MkplCommand.CREATE, request, logger, "ad-create")

    @PostMapping("read")
    suspend fun  readAd(@RequestBody request: AdReadRequest): AdReadResponse =
        processV1(processor, MkplCommand.READ, request, logger, "ad-read")

    @RequestMapping("update", method = [RequestMethod.POST])
    suspend fun  updateAd(@RequestBody request: AdUpdateRequest): AdUpdateResponse =
        processV1(processor, MkplCommand.UPDATE, request, logger, "ad-update")

    @PostMapping("delete")
    suspend fun  deleteAd(@RequestBody request: AdDeleteRequest): AdDeleteResponse =
        processV1(processor, MkplCommand.DELETE, request, logger, "ad-delete")

    @PostMapping("search")
    suspend fun  searchAd(@RequestBody request: AdSearchRequest): AdSearchResponse =
        processV1(processor, MkplCommand.SEARCH, request, logger, "ad-search")
}
