package ru.otus.otuskotlin.marketplace.blackbox.rest.stub

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import ru.otus.otuskotlin.marketplace.blackbox.AppContainer

class AccRestStub : BehaviorSpec({
    // Временно отключаем завал до момента реализации функциональности
    severity = TestCaseSeverityLevel.MINOR

    val client = HttpClient(OkHttp)
    given("I am going to create an Ad") {
        `when`("I send empty object") {
            val result = client
                .get {
                    url(AppContainer.C.url)
                    accept(ContentType.Application.Json)
//                    contentType(ContentType.Application.Json)
//                    parameter("xx", "gg")
                }
                .call
            val resp = result.response

            then("Response status is 200") {
                resp.status.value shouldBe 200
            }
            then("I get validation error") {
                // Здесь будет проверка на ошибку валидации в ответе
                // Пока будет просто заваливаться
                resp.bodyAsText() shouldContain "Error"
            }
            xthen("Error contains word \"validation\"") {
                // Здесь будет проверка на ошибку валидации в ответе
                // Просто отключаем тест
                resp.bodyAsText() shouldContain "validation"
            }
        }

    }
})
