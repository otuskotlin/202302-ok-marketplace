package ru.otus.otuskotlin.marketplace.blackbox.test

import fixture.rest.RestClient
import io.kotest.core.annotation.Ignored
import ru.otus.otuskotlin.marketplace.blackbox.docker.KtorDockerCompose
import ru.otus.otuskotlin.marketplace.blackbox.docker.SpringDockerCompose
import ru.otus.otuskotlin.marketplace.blackbox.fixture.BaseFunSpec
import ru.otus.otuskotlin.marketplace.blackbox.fixture.DockerCompose

@Ignored
open class AccRestTestBase(dockerCompose: DockerCompose) : BaseFunSpec(dockerCompose, {
    val client = RestClient(dockerCompose)

    testApiV1(client)
    testApiV2(client)
})

class AccRestSpringTest : AccRestTestBase(SpringDockerCompose)
class AccRestKtorTest : AccRestTestBase(KtorDockerCompose)
