package ru.otus.otuskotlin.marketplace.blackbox.test.action.v2

import ru.otus.otuskotlin.marketplace.api.v2.apiV2RequestSerialize
import ru.otus.otuskotlin.marketplace.api.v2.apiV2ResponseDeserialize
import ru.otus.otuskotlin.marketplace.api.v2.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v2.models.IResponse
import ru.otus.otuskotlin.marketplace.blackbox.fixture.Client

suspend fun Client.sendAndReceive(path: String, request: IRequest): IResponse {
    val requestBody = apiV2RequestSerialize(request)
    println("Send to v2/$path\n$requestBody")

    val responseBody = sendAndReceive("v2", path, requestBody)
    println("Received\n$responseBody")

    return apiV2ResponseDeserialize(responseBody)
}