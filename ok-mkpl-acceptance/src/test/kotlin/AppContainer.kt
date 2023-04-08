package ru.otus.otuskotlin.marketplace.blackbox

import io.ktor.http.*
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

@Suppress("unused")
class AppContainer private constructor() {
    private val _port = 80

    private val container = GenericContainer(DockerImageName.parse("nginx:latest")).apply {
        withExposedPorts(_port)
        start()
    }

    val host: String = container.host
    val port: Int = container.getMappedPort(_port)

    val url: Url = URLBuilder(
        protocol = URLProtocol.HTTP,
        host = host,
        port = port,
    ).build()

    fun close() {
        container.close()
    }

    companion object {
        val C by lazy { AppContainer() }
    }
}
