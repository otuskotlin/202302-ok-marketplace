package ru.otus.otuskotlin.marketplace.blackbox.test.action.v1

import io.kotest.assertions.asClue
import io.kotest.assertions.withClue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import ru.otus.otuskotlin.marketplace.api.v1.models.*
import ru.otus.otuskotlin.marketplace.blackbox.fixture.Client

suspend fun Client.createAd(ad: AdCreateObject = someCreateAd): AdResponseObject = createAd(ad) {
    it should haveSuccessResult
    it.ad shouldBe adStub
    it.ad!!
}

suspend fun <T> Client.createAd(ad: AdCreateObject = someCreateAd, block: (AdCreateResponse) -> T): T =
    withClue("createAdV1: $ad") {
        val response = sendAndReceive(
            "ad/create", AdCreateRequest(
                requestType = "create",
                debug = debug,
                ad = ad
            )
        ) as AdCreateResponse

        response.asClue(block)
    }
