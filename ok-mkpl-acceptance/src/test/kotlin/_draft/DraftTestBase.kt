package ru.otus.otuskotlin.marketplace.blackbox._draft

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseSeverityLevel
import io.kotest.matchers.shouldBe
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import ru.otus.otuskotlin.marketplace.blackbox.AppContainer

@Ignored
open class DraftTestBase(
    prop: String,
) : FunSpec({
    val client = HttpClient(OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
        }
    }
    test("$prop: my first test").config(severity = TestCaseSeverityLevel.MINOR) {
        1 + 2 shouldBe 4
    }

    test("$prop: container test") {
        val result = client
            .get {
                url("http://${AppContainer.C.host}:${AppContainer.C.port}")
            }
            .call
        val resp = result.response
        resp.status.value shouldBe 200
    }
})
