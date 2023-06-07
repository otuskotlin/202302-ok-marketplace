package ru.otus.otuskotlin.markeplace.springapp.api.v2.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.otus.otuskotlin.marketplace.api.v2.models.AdOffersRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdOffersResponse
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand

@RestController
@RequestMapping("v2/ad")
class OfferControllerV2(
    private val processor: MkplAdProcessor,
    settings: MkplCorSettings
) {
    private val logger = settings.loggerProvider.logger(OfferControllerV2::class)

    @PostMapping("offers")
    suspend fun searchOffers(@RequestBody request: String): String =
        processV2<AdOffersRequest, AdOffersResponse>(processor, MkplCommand.OFFERS, request, logger, "ad-offers")
}

