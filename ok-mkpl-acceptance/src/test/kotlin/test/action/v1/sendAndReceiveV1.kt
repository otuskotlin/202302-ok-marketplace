package ru.otus.otuskotlin.marketplace.blackbox.test.action.v1

import ru.otus.otuskotlin.marketplace.api.v1.apiV1RequestSerialize
import ru.otus.otuskotlin.marketplace.api.v1.apiV1ResponseDeserialize
import ru.otus.otuskotlin.marketplace.api.v1.models.IRequest
import ru.otus.otuskotlin.marketplace.api.v1.models.IResponse
import ru.otus.otuskotlin.marketplace.blackbox.fixture.Client

suspend fun Client.sendAndReceive(path: String, request: IRequest): IResponse {
    val requestBody = apiV1RequestSerialize(request)
    println("Send to v1/$path\n$requestBody")

    val responseBody = sendAndReceive("v1", path, requestBody)
    println("Received\n$responseBody")

    return apiV1ResponseDeserialize(responseBody)
}