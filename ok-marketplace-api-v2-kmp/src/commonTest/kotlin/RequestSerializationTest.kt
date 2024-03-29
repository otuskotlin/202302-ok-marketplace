package ru.otus.otuskotlin.marketplace.api.v2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.otus.otuskotlin.marketplace.api.v2.models.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class RequestSerializationTest {
    private val request : IRequest = AdCreateRequest(
        requestType = "create",
        requestId = "123",
        debug = AdDebug(
            mode = AdRequestDebugMode.STUB,
            stub = AdRequestDebugStubs.BAD_TITLE
        ),
        ad = AdCreateObject(
            title = "ad title",
            description = "ad description",
            adType = DealSide.DEMAND,
            visibility = AdVisibility.PUBLIC,
        )
    )

    @Test
    fun serialize() {
//        val json = apiV2RequestSerialize(AdRequestSerializer1, request)
//        val json = apiV2RequestSerialize(RequestSerializers.create, request)
        val json = apiV2RequestSerialize(request)

        println(json)

        assertContains(json, Regex("\"title\":\\s*\"ad title\""))
        assertContains(json, Regex("\"mode\":\\s*\"stub\""))
        assertContains(json, Regex("\"stub\":\\s*\"badTitle\""))
        assertContains(json, Regex("\"requestType\":\\s*\"create\""))
    }

    @Test
    fun serializeWithoutType() {
        val json = apiV2RequestSerialize((request as AdCreateRequest).copy(requestType = null))

        println(json)

        assertContains(json, Regex("\"title\":\\s*\"ad title\""))
        assertContains(json, Regex("\"mode\":\\s*\"stub\""))
        assertContains(json, Regex("\"stub\":\\s*\"badTitle\""))
        assertContains(json, Regex("\"requestType\":\\s*\"create\""))
    }

    @Test
    fun deserialize() {
        val json = apiV2RequestSerialize(request)
//        val json = apiV2RequestSerialize(AdRequestSerializer1, request)
//        val json = apiV2RequestSerialize(RequestSerializers.create, request)
//        val obj = apiV2RequestDeserialize(AdRequestSerializer, json) as AdCreateRequest
        val obj = apiV2RequestDeserialize(json) as AdCreateRequest

        assertEquals(request, obj)
    }
    @Test
    fun deserializeNaked() {
        val jsonString = """
            {
            "requestType":"create",
            "requestId":"123",
            "debug":{"mode":"stub","stub":"badTitle"},
            "ad":{"title":"ad title","description":"ad description","adType":"demand","visibility":"public","productId":null}
            }
        """.trimIndent()
        val obj = apiV2RequestDeserialize(jsonString) as IRequest

        assertEquals("123", obj.requestId)
        assertEquals(request, obj)
    }
}
