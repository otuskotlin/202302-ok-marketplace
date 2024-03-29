package ru.otus.otuskotlin.marketplace.app.inmemory

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.otus.otuskotlin.marketplace.api.v2.apiV2Mapper
import ru.otus.otuskotlin.marketplace.api.v2.models.*
import ru.otus.otuskotlin.marketplace.app.base.KtorAuthConfig
import ru.otus.otuskotlin.marketplace.app.helpers.testSettings
import ru.otus.otuskotlin.marketplace.app.module
import ru.otus.otuskotlin.marketplace.app.ru.otus.otuskotlin.marketplace.auth.addAuth
import ru.otus.otuskotlin.marketplace.common.models.MkplAdId
import ru.otus.otuskotlin.marketplace.common.models.MkplAdLock
import ru.otus.otuskotlin.marketplace.common.models.MkplDealSide
import ru.otus.otuskotlin.marketplace.common.models.MkplVisibility
import ru.otus.otuskotlin.marketplace.repo.inmemory.AdRepoInMemory
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class V2AdInmemoryApiTest {
    private val uuidOld = "10000000-0000-0000-0000-000000000001"
    private val uuidNew = "10000000-0000-0000-0000-000000000002"
    private val uuidSup = "10000000-0000-0000-0000-000000000003"
    private val initAd = MkplAdStub.prepareResult {
        id = MkplAdId(uuidOld)
        title = "abc"
        description = "abc"
        adType = MkplDealSide.DEMAND
        visibility = MkplVisibility.VISIBLE_PUBLIC
        lock = MkplAdLock(uuidOld)
    }
    private val initAdSupply = MkplAdStub.prepareResult {
        id = MkplAdId(uuidSup)
        title = "abc"
        description = "abc"
        adType = MkplDealSide.SUPPLY
        visibility = MkplVisibility.VISIBLE_PUBLIC
    }

    private val userId = initAd.ownerId

    @Test
    fun create() = testApplication {
        application { module(testSettings(AdRepoInMemory(randomUuid = { uuidNew }))) }

        val createAd = AdCreateObject(
            title = "Болт",
            description = "КРУТЕЙШИЙ",
            adType = DealSide.DEMAND,
            visibility = AdVisibility.PUBLIC,
        )

        val response = client.post("/v2/ad/create") {
            val requestObj = AdCreateRequest(
                requestId = "12345",
                ad = createAd,
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdCreateResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertEquals(uuidNew, responseObj.ad?.id)
        assertEquals(createAd.title, responseObj.ad?.title)
        assertEquals(createAd.description, responseObj.ad?.description)
        assertEquals(createAd.adType, responseObj.ad?.adType)
        assertEquals(createAd.visibility, responseObj.ad?.visibility)
    }

    @Test
    fun read() = testApplication {
        val repo = AdRepoInMemory(initObjects = listOf(initAd), randomUuid = { uuidNew })
        application {
            module(testSettings(repo))
        }

        val response = client.post("/v2/ad/read") {
            val requestObj = AdReadRequest(
                requestId = "12345",
                ad = AdReadObject(uuidOld),
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdReadResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.ad?.id)
    }

    @Test
    fun update() = testApplication {
        val repo = AdRepoInMemory(initObjects = listOf(initAd), randomUuid = { uuidNew })
        application {
            module(testSettings(repo))
        }

        val adUpdate = AdUpdateObject(
            id = uuidOld,
            title = "Болт",
            description = "КРУТЕЙШИЙ",
            adType = DealSide.DEMAND,
            visibility = AdVisibility.PUBLIC,
            lock = initAd.lock.asString(),
        )

        val response = client.post("/v2/ad/update") {
            val requestObj = AdUpdateRequest(
                requestId = "12345",
                ad = adUpdate,
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdUpdateResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertEquals(adUpdate.id, responseObj.ad?.id)
        assertEquals(adUpdate.title, responseObj.ad?.title)
        assertEquals(adUpdate.description, responseObj.ad?.description)
        assertEquals(adUpdate.adType, responseObj.ad?.adType)
        assertEquals(adUpdate.visibility, responseObj.ad?.visibility)
    }

    @Test
    fun delete() = testApplication {
        val repo = AdRepoInMemory(initObjects = listOf(initAd), randomUuid = { uuidNew })
        application {
            module(testSettings(repo))
        }

        val response = client.post("/v2/ad/delete") {
            val requestObj = AdDeleteRequest(
                requestId = "12345",
                ad = AdDeleteObject(
                    id = uuidOld,
                    lock = initAd.lock.asString(),
                ),
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdDeleteResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertEquals(uuidOld, responseObj.ad?.id)
    }

    @Test
    fun search() = testApplication {
        val repo = AdRepoInMemory(initObjects = listOf(initAd), randomUuid = { uuidNew })
        application {
            module(testSettings(repo))
        }

        val response = client.post("/v2/ad/search") {
            val requestObj = AdSearchRequest(
                requestId = "12345",
                adFilter = AdSearchFilter(),
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdSearchResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertNotEquals(0, responseObj.ads?.size)
        assertEquals(uuidOld, responseObj.ads?.first()?.id)
    }

    @Test
    fun offers() = testApplication {
        val repo = AdRepoInMemory(initObjects = listOf(initAd, initAdSupply), randomUuid = { uuidNew })
        application {
            module(testSettings(repo))
        }

        val response = client.post("/v2/ad/offers") {
            val requestObj = AdOffersRequest(
                requestId = "12345",
                ad = AdReadObject(
                    id = uuidOld,
                ),
                debug = AdDebug(
                    mode = AdRequestDebugMode.TEST,
                )
            )
            contentType(ContentType.Application.Json)
            addAuth(id = userId.asString(), config = KtorAuthConfig.TEST)
            val requestJson = apiV2Mapper.encodeToString(requestObj)
            setBody(requestJson)
        }
        val responseJson = response.bodyAsText()
        val responseObj = apiV2Mapper.decodeFromString<AdOffersResponse>(responseJson)
        assertEquals(200, response.status.value)
        assertNotEquals(0, responseObj.ads?.size)
        assertEquals(uuidSup, responseObj.ads?.first()?.id)
    }
}
