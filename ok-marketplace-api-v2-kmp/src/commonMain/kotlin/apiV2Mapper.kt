@file:OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)

package ru.otus.otuskotlin.marketplace.api.v2

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual
import ru.otus.otuskotlin.marketplace.api.v2.models.AdCreateRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdCreateResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdDeleteRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdDeleteResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdInitResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdOffersRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdOffersResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdReadRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdReadResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdSearchRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdSearchResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.AdUpdateRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.AdUpdateResponse
import ru.otus.otuskotlin.marketplace.api.v2.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.IResponse
import kotlin.reflect.KClass

private data class PolymorphicInfo<T : Any>(
    val klass: KClass<T>,
    val serialize: (Encoder, Any) -> Unit,
    val discriminator: String,
    val makeCopyWithDiscriminator: (Any) -> T
)

@Suppress("UNCHECKED_CAST")
private fun <T: Any> info(klass: KClass<T>,
                          discriminator: String,
                          makeCopyWithDiscriminator: (T, String) -> T) =
    PolymorphicInfo(
        klass,
        {e : Encoder, v : Any ->
            klass.serializer().serialize(e, v as T)
        },
        discriminator,
        {v : Any ->
            makeCopyWithDiscriminator(v as T, discriminator)
        }
    )

private val infos = listOf(
    info(AdCreateRequest::class, "create") { v, d -> v.copy(requestType = d)},
    info(AdReadRequest::class, "read") { v, d -> v.copy(requestType = d)},
    info(AdUpdateRequest::class, "update") { v, d -> v.copy(requestType = d)},
    info(AdDeleteRequest::class, "delete") { v, d -> v.copy(requestType = d)},
    info(AdSearchRequest::class, "search") { v, d -> v.copy(requestType = d)},
    info(AdOffersRequest::class, "offers") { v, d -> v.copy(requestType = d)},

    info(AdCreateResponse::class, "create") { v, d -> v.copy(responseType = d)},
    info(AdReadResponse::class, "read") { v, d -> v.copy(responseType = d)},
    info(AdUpdateResponse::class, "update") { v, d -> v.copy(responseType = d)},
    info(AdDeleteResponse::class, "delete") { v, d -> v.copy(responseType = d)},
    info(AdSearchResponse::class, "search") { v, d -> v.copy(responseType = d)},
    info(AdOffersResponse::class, "offers") { v, d -> v.copy(responseType = d)},
    info(AdInitResponse::class, "init")  { v, d -> v.copy(responseType = d)},
)

private inline fun <reified T : Any> SerializersModuleBuilder.polymorphicSerializer() {
    polymorphicDefaultSerializer(T::class) { value ->
        val info = infos.firstOrNull { it.klass == value::class } ?: throw SerializationException(
            "Unknown class to serialize ${value::class}"
        )
        object : KSerializer<T> {
            override val descriptor: SerialDescriptor
                get() = info.klass.serializer().descriptor

            override fun serialize(encoder: Encoder, value: T) {
                val copy = info.makeCopyWithDiscriminator(value)
                info.serialize(encoder, copy)
            }

            override fun deserialize(decoder: Decoder): T = throw NotImplementedError("you should not use this method")
        }
    }
}

private class PolymorphicSerializer<T : Any>(cls: KClass<T>, val discriminatorField: String) :
    JsonContentPolymorphicSerializer<T>(cls) {
    @Suppress("UNCHECKED_CAST")
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out T> {
        val discriminatorValue = element.jsonObject[discriminatorField]?.jsonPrimitive?.content
        val info = infos.firstOrNull { it.discriminator == discriminatorValue } ?: throw SerializationException(
            "Unknown class to deserialize: $discriminatorValue"
        )
        return info.klass.serializer() as DeserializationStrategy<out T>
    }
}

private val requestSerializer = PolymorphicSerializer(IRequest::class, "requestType")
private val responseSerializer = PolymorphicSerializer(IResponse::class, "responseType")

val apiV2Mapper = Json {
    classDiscriminator = "_"
    encodeDefaults = true
    ignoreUnknownKeys = true

    serializersModule = SerializersModule {
        polymorphicSerializer<IRequest>()
        polymorphicDefaultDeserializer(IRequest::class) { requestSerializer }

        polymorphicSerializer<IResponse>()
        polymorphicDefaultDeserializer(IResponse::class) { responseSerializer }

        contextual(requestSerializer)
        contextual(responseSerializer)
    }
}

