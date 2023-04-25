package ru.otus.otuskotlin.marketplace.api.v2

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ru.otus.otuskotlin.marketplace.api.v2.models.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ResponseSerializationTest {
    private val response: IResponse = AdCreateResponse(
        responseType = "create",
        requestId = "123",
        ad = AdResponseObject(
            title = "ad title",
            description = "ad description",
            adType = DealSide.DEMAND,
            visibility = AdVisibility.PUBLIC,
        )
    )

    @Test
    fun serialize() {
//        val json = apiV2ResponseSerialize(AdRequestSerializer1, request)
//        val json = apiV2ResponseSerialize(RequestSerializers.create, request)
        val json = apiV2ResponseSerialize(response)

        println(json)

        assertContains(json, Regex("\"title\":\\s*\"ad title\""))
        assertContains(json, Regex("\"responseType\":\\s*\"create\""))
    }

    @Test
    fun deserialize() {
        val json = apiV2ResponseSerialize(response)
//        val json = apiV2ResponseSerialize(AdRequestSerializer1, request)
//        val json = apiV2ResponseSerialize(RequestSerializers.create, request)
//        val obj = apiV2Mapper.decodeFromString(AdRequestSerializer, json) as AdCreateRequest
        val obj = apiV2ResponseDeserialize(json) as AdCreateResponse

        assertEquals(response, obj)
    }

    @Test
    fun deserializeNaked() {
        val jsonString = """
            {
            "responseType":"create",
            "requestId":"123",
            "result":null,
            "errors":null,
            "ad":{"title":"ad title","description":"ad description","adType":"demand","visibility":"public","productId":null,"id":null,"ownerId":null,"lock":null,"permissions":null}
            }
        """.trimIndent()
        val obj = apiV2ResponseDeserialize(jsonString) as IResponse

        assertEquals("123", obj.requestId)
        assertEquals(response, obj)
    }
}
