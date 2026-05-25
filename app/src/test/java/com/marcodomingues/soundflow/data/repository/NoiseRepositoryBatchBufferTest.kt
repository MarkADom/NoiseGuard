package com.marcodomingues.noiseguard.data.repository

import com.marcodomingues.noiseguard.data.local.NoiseLevelDao
import com.marcodomingues.noiseguard.data.local.NoiseLevelEntity
import com.marcodomingues.noiseguard.data.repository.NoiseRepositoryImpl
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NoiseRepositoryBatchBufferTest {

    private val dao = mockk<NoiseLevelDao>(relaxed = true)
    private lateinit var repository: NoiseRepositoryImpl

    @Before
    fun setUp() {
        repository = NoiseRepositoryImpl(dao)
    }

    @Test
    fun `9 readings do not trigger a DAO write`() = runTest {
        repeat(9) { repository.saveReading(NoiseLevel(65.0)) }
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `10th reading triggers exactly one batch insert`() = runTest {
        repeat(10) { repository.saveReading(NoiseLevel(65.0)) }
        coVerify(exactly = 1) { dao.insertAll(any()) }
    }

    @Test
    fun `batch insert contains exactly 10 readings`() = runTest {
        val captured = slot<List<NoiseLevelEntity>>()
        coEvery { dao.insertAll(capture(captured)) } returns Unit

        repeat(10) { repository.saveReading(NoiseLevel(65.0)) }

        assertEquals(10, captured.captured.size)
    }

    @Test
    fun `20 readings trigger exactly 2 batch inserts`() = runTest {
        repeat(20) { repository.saveReading(NoiseLevel(65.0)) }
        coVerify(exactly = 2) { dao.insertAll(any()) }
    }

    @Test
    fun `flushBuffer writes buffered readings to DAO`() = runTest {
        repeat(7) { repository.saveReading(NoiseLevel(65.0)) }
        coVerify(exactly = 0) { dao.insertAll(any()) }

        repository.flushBuffer()

        coVerify(exactly = 1) { dao.insertAll(any()) }
    }

    @Test
    fun `flushBuffer on empty buffer does not call DAO`() = runTest {
        repository.flushBuffer()
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `second flushBuffer after first is a no-op`() = runTest {
        repeat(5) { repository.saveReading(NoiseLevel(65.0)) }

        repository.flushBuffer()
        repository.flushBuffer()

        coVerify(exactly = 1) { dao.insertAll(any()) }
    }

    @Test
    fun `deleteAllReadings clears the in-memory buffer`() = runTest {
        repeat(5) { repository.saveReading(NoiseLevel(65.0)) }

        repository.deleteAllReadings()

        // Buffer should be empty — a subsequent flush must not write anything
        repository.flushBuffer()
        coVerify(exactly = 0) { dao.insertAll(any()) }
    }

    @Test
    fun `deleteAllReadings calls DAO deleteAll`() = runTest {
        repository.deleteAllReadings()
        coVerify(exactly = 1) { dao.deleteAll() }
    }
}
