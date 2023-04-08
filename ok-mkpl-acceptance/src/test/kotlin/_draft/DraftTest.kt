package ru.otus.otuskotlin.marketplace.blackbox._draft

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import ru.otus.otuskotlin.marketplace.blackbox.AppContainer

class DraftTest : FunSpec({
    val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }
    test("my first test").config(severity = TestCaseSeverityLevel.MINOR) {
        1 + 2 shouldBe 4
    }

    test("container test") {
        val result = client
            .get {
                url(AppContainer.C.url)
            }
            .call
        val resp = result.response
        resp.status.value shouldBe 200
    }
})
