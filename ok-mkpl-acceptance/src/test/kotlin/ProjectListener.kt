package ru.otus.otuskotlin.marketplace.blackbox

import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

@Suppress("unused")
@AutoScan
object ProjectListener : BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        println("Project starting")
//        val port = AppContainer.C.port
        val host = AppCompose.C.hostApp
        val port = AppCompose.C.portApp
        println("Started docker-compose with App at HOST: $host PORT: $port")
    }
    override suspend fun afterProject() {
//        AppContainer.C.close()
        AppCompose.C.close()
        println("Project complete")
    }
}
