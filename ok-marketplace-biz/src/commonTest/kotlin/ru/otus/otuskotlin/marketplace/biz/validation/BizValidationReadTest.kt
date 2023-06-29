package ru.otus.otuskotlin.marketplace.biz.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplCorSettings
import ru.otus.otuskotlin.marketplace.common.models.MkplCommand
import ru.otus.otuskotlin.marketplace.repo.inmemory.AdRepoInMemory
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BizValidationReadTest {

    private val command = MkplCommand.READ
    private lateinit var processor:MkplAdProcessor
    @BeforeTest
    fun beforeEach(){
        val repoTest = AdRepoInMemory(initObjects = listOf(MkplAdStub.get()))
        processor = MkplAdProcessor(MkplCorSettings(repoTest = repoTest))
    }

    @Test fun correctId() = validationIdCorrect(command, processor)
    @Test fun trimId() = validationIdTrim(command, processor)
    @Test fun emptyId() = validationIdEmpty(command, processor)
    @Test fun badFormatId() = validationIdFormat(command, processor)

}

