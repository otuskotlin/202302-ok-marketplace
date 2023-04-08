package ru.otus.otuskotlin.marketplace.blackbox.rest.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import ru.otus.otuskotlin.marketplace.blackbox.AppContainer

class AccRestTest : FunSpec({
    // Временно отключаем завал до момента реализации функциональности
    severity = TestCaseSeverityLevel.MINOR

    val client = HttpClient(OkHttp)

    test("Create") {
        withData(
            mapOf(
                "TC-1" to Pair(Request(), Response()),
                "TC-2" to Pair(Request(), Response()),
                "TC-3" to Pair(Request(), Response()),
            )
        ) { (req, expected) ->
            val resp = client.get {
                url(AppContainer.C.url)
                accept(ContentType.Application.Json)
                setBody(req)
            }.call
            val actual = resp.body<Response>()
            actual shouldBe expected
        }
    }
})

data class Request(
    val title: String? = null,
    val description: String? = null,
    val state: String? = null,
)

data class Response(
    val result: String? = null,
    val data: Request? = null,
)
