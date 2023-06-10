package ru.otus.otuskotlin.marketplace.common.repo

import ru.otus.otuskotlin.marketplace.common.models.MkplAd
import ru.otus.otuskotlin.marketplace.common.models.MkplAdId
import ru.otus.otuskotlin.marketplace.common.models.MkplAdLock

data class DbAdIdRequest(
    val id: MkplAdId,
) {
    constructor(ad: MkplAd): this(ad.id)
}