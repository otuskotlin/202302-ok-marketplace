package ru.otus.otuskotlin.marketplace.biz.validation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import ru.otus.otuskotlin.marketplace.biz.MkplAdProcessor
import ru.otus.otuskotlin.marketplace.common.MkplContext
import ru.otus.otuskotlin.marketplace.common.models.*
import ru.otus.otuskotlin.marketplace.stubs.MkplAdStub
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@OptIn(ExperimentalCoroutinesApi::class)
fun validationIdCorrect(command: MkplCommand, processor: MkplAdProcessor) = runTest {
    val ctx = MkplContext(
        command = command,
        state = MkplState.NONE,
        workMode = MkplWorkMode.TEST,
        adRequest = MkplAdStub.get(),
    )
    processor.exec(ctx)
    assertEquals(0, ctx.errors.size)
    assertNotEquals(MkplState.FAILING, ctx.state)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun validationIdTrim(command: MkplCommand, processor: MkplAdProcessor) = runTest {
    val adRequest = MkplAdStub.get()
    adRequest.id = MkplAdId(" \n\t 666 \n\t ")
    val ctx = MkplContext(
        command = command,
        state = MkplState.NONE,
        workMode = MkplWorkMode.TEST,
        adRequest = adRequest,
    )
    processor.exec(ctx)
    assertEquals(0, ctx.errors.size)
    assertNotEquals(MkplState.FAILING, ctx.state)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun validationIdEmpty(command: MkplCommand, processor: MkplAdProcessor) = runTest {
    val adRequest = MkplAdStub.get()
    adRequest.id = MkplAdId("")
    val ctx = MkplContext(
        command = command,
        state = MkplState.NONE,
        workMode = MkplWorkMode.TEST,
        adRequest = adRequest,
    )
    processor.exec(ctx)
    assertEquals(1, ctx.errors.size)
    assertEquals(MkplState.FAILING, ctx.state)
    val error = ctx.errors.firstOrNull()
    assertEquals("id", error?.field)
    assertContains(error?.message ?: "", "id")
}

@OptIn(ExperimentalCoroutinesApi::class)
fun validationIdFormat(command: MkplCommand, processor: MkplAdProcessor) = runTest {
    val adRequest = MkplAdStub.get()
    adRequest.id = MkplAdId("!@#\$%^&*(),.{}")
    val ctx = MkplContext(
        command = command,
        state = MkplState.NONE,
        workMode = MkplWorkMode.TEST,
        adRequest = adRequest
    )
    processor.exec(ctx)
    assertEquals(1, ctx.errors.size)
    assertEquals(MkplState.FAILING, ctx.state)
    val error = ctx.errors.firstOrNull()
    assertEquals("id", error?.field)
    assertContains(error?.message ?: "", "id")
}
