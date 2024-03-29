package ru.otus.otuskotlin.markeplace.springapp.api.v2.controller

import org.springframework.web.bind.annotation.*
import ru.otus.otuskotlin.marketplace.api.v2.models.*
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.mappers.v2.*

@RestController
@RequestMapping("v2/ad")
class AdControllerV2(
    private val processor: MkplAdProcessor,
    settings: MkplCorSettings
) {
    private val logger = settings.loggerProvider.logger(AdControllerV2::class)

    @PostMapping("create")
    suspend fun createAd(@RequestBody request: String): String =
        processV2<AdCreateRequest, AdCreateResponse>(processor, MkplCommand.CREATE, request, logger, "ad-create")

    @PostMapping("read")
    suspend fun readAd(@RequestBody request: String): String =
        processV2<AdReadRequest, AdCreateResponse>(processor, MkplCommand.CREATE, request, logger, "ad-create")

    @PostMapping("update")
    suspend fun updateAd(@RequestBody request: String): String =
        processV2<AdUpdateRequest, AdUpdateResponse>(processor, MkplCommand.UPDATE, request, logger, "ad-update")

    @PostMapping("delete")
    suspend fun deleteAd(@RequestBody request: String): String =
        processV2<AdDeleteRequest, AdDeleteResponse>(processor, MkplCommand.DELETE, request, logger, "ad-delete")

    @PostMapping("search")
    suspend fun searchAd(@RequestBody request: String): String =
        processV2<AdSearchRequest, AdSearchResponse>(processor, MkplCommand.SEARCH,  request, logger, "ad-search")
}
