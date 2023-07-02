package ru.otus.otuskotlin.marketplace.backend.repository.gremlin

import org.junit.Ignore
import ru.otus.otuskotlin.marketplace.backend.repo.tests.RepoAdReadTest
import ru.otus.otuskotlin.marketplace.common.models.MkplAd
@Ignore
class AdRepoGremlinReadTest : RepoAdReadTest() {
    override val repo: AdRepoGremlin by lazy {
        AdRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            user = ArcadeDbContainer.username,
            pass = ArcadeDbContainer.password,
            enableSsl = false,
            initObjects = initObjects,
            initRepo = { g -> g.V().drop().iterate() },
        )
    }
    override val readSucc: MkplAd by lazy { repo.initializedObjects[0] }
}
