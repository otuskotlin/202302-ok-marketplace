package ru.otus.otuskotlin.marketplace.app.rabbit.config

import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.logging.common.MpLoggerProvider
import ru.otus.otuskotlin.marketplace.logging.jvm.mpLoggerLogback

private val loggerProvider = MpLoggerProvider { mpLoggerLogback(it) }

val corSettings = MkplCorSettings(
    loggerProvider = loggerProvider
)