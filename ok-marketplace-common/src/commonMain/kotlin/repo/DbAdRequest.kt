package ru.otus.otuskotlin.marketplace.common.repo

import ru.otus.otuskotlin.marketplace.common.models.MkplAd
import ru.otus.otuskotlin.marketplace.common.models.MkplAdLock

data class DbAdRequest(
    val ad: MkplAd,
    var lock: MkplAdLock = MkplAdLock.NONE,
)
