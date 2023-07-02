package ru.otus.otuskotlin.marketplace.backend.repository.gremlin

import org.junit.Ignore
import ru.otus.otuskotlin.marketplace.backend.repo.tests.RepoAdSearchTest
import ru.otus.otuskotlin.marketplace.common.models.MkplAd
@Ignore
class AdRepoGremlinSearchTest: RepoAdSearchTest() {
    override val repo: AdRepoGremlin by lazy {
        AdRepoGremlin(
            hosts = ArcadeDbContainer.container.host,
            port = ArcadeDbContainer.container.getMappedPort(8182),
            enableSsl = false,
            user = ArcadeDbContainer.username,
            pass = ArcadeDbContainer.password,
            initObjects = initObjects,
            initRepo = { g -> g.V().drop().iterate() },
        )
    }

    override val initializedObjects: List<MkplAd> by lazy {
        repo.initializedObjects
    }
}
