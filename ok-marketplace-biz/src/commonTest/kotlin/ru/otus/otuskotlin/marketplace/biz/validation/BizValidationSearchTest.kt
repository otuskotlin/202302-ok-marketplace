package ru.otus.otuskotlin.marketplace.biz.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.*
import ru.otus.otuskotlin.marketplace.repo.inmemory.AdRepoInMemory
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
class BizValidationSearchTest {

    private val command = MkplCommand.SEARCH
    private lateinit var processor:MkplAdProcessor
    @BeforeTest
    fun beforeEach(){
        val repoTest = AdRepoInMemory(initObjects = listOf(MkplAdStub.get()))
        processor = MkplAdProcessor(MkplCorSettings(repoTest = repoTest))
    }

    @Test
    fun correctEmpty() = runTest {
        val ctx = MkplContext(
            command = command,
            state = MkplState.NONE,
            workMode = MkplWorkMode.TEST,
            adFilterRequest = MkplAdFilter()
        )
        processor.exec(ctx)
        assertEquals(0, ctx.errors.size)
        assertNotEquals(MkplState.FAILING, ctx.state)
    }
}

