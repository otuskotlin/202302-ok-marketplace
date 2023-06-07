package ru.otus.otuskotlin.markeplace.springapp.api.v1.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.otuskotlin.marketplace.api.v1.models.AdOffersRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.AdOffersResponse
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand

@RestController
@RequestMapping("v1/ad")
class OfferController(
    private val processor: MkplAdProcessor,
    settings: MkplCorSettings
) {
    private val logger = settings.loggerProvider.logger(OfferController::class)

    @PostMapping("offers")
    suspend fun searchOffers(@RequestBody request: AdOffersRequest): AdOffersResponse =
        processV1(processor, MkplCommand.OFFERS, request, logger, "ad-offers")
}
