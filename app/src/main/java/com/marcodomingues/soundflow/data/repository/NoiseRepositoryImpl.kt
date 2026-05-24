package com.marcodomingues.noiseguard.data.repository

import com.marcodomingues.noiseguard.data.local.NoiseLevelDao
import com.marcodomingues.noiseguard.data.local.NoiseLevelEntity
import com.marcodomingues.noiseguard.domain.model.NoiseCategory
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Buffers readings in a Mutex-guarded list and flushes to Room every 10 entries.
 * At 100ms sampling this cuts Room transactions from 10/s to 1/s.
 */
class NoiseRepositoryImpl(
    private val dao: NoiseLevelDao
) : NoiseRepository {

    // The MutableList is not thread-safe on its own; the Mutex guards all access.
    private val batchBuffer = mutableListOf<NoiseLevelEntity>()
    private val bufferMutex = Mutex()

    companion object {
        private const val BATCH_SIZE = 10
        private const val CLEANUP_THRESHOLD_DAYS = 30L
    }

    override suspend fun saveReading(noiseLevel: NoiseLevel) {
        withContext(Dispatchers.IO) {
            val entity = NoiseLevelEntity(
                decibels = noiseLevel.decibels,
                timestamp = System.currentTimeMillis(),
                category = noiseLevel.category.name
            )

            bufferMutex.withLock {
                batchBuffer.add(entity)

                if (batchBuffer.size >= BATCH_SIZE) {
                    val toInsert = batchBuffer.toList()
                    batchBuffer.clear()
                    dao.insertAll(toInsert)
                }
            }

            cleanupOldReadingsIfNeeded()
        }
    }

    /**
     * Writes any buffered readings to Room. Call this when monitoring stops —
     * the buffer won't be flushed automatically until the next batch of 10.
     */
    override suspend fun flushBuffer() {
        withContext(Dispatchers.IO) {
            bufferMutex.withLock {
                if (batchBuffer.isNotEmpty()) {
                    val toInsert = batchBuffer.toList()
                    batchBuffer.clear()
                    dao.insertAll(toInsert)
                }
            }
        }
    }

    /**
     * Maps Room entities to domain models so callers never touch the data layer directly.
     */
    override fun getReadingsSince(timestamp: Long): Flow<List<NoiseLevel>> {
        return dao.getReadingsSince(timestamp).map { entities ->
            entities.map { entity ->
                NoiseLevel(
                    decibels = entity.decibels,
                    timestamp = entity.timestamp,
                    category = NoiseCategory.valueOf(entity.category)
                )
            }
        }
    }

    override suspend fun getAverageSince(timestamp: Long): Double? {
        return withContext(Dispatchers.IO) {
            dao.getAverageSince(timestamp)
        }
    }

    override suspend fun getPeakSince(timestamp: Long): Double? {
        return withContext(Dispatchers.IO) {
            dao.getPeakSince(timestamp)
        }
    }

    /**
     * Clears the in-memory buffer before hitting the DAO, so a subsequent
     * flush after deleteAllReadings() doesn't re-insert stale entries.
     */
    override suspend fun deleteAllReadings() {
        withContext(Dispatchers.IO) {
            bufferMutex.withLock {
                batchBuffer.clear()
            }
            dao.deleteAll()
        }
    }

    // Rate-limits the 30-day cleanup to at most once per hour.
    // AtomicLong because this can be read from different coroutines concurrently.
    private val lastCleanupTime = AtomicLong(0L)

    private suspend fun cleanupOldReadingsIfNeeded() {
        val now = System.currentTimeMillis()
        val oneHour = TimeUnit.HOURS.toMillis(1)

        if (now - lastCleanupTime.get() > oneHour) {
            lastCleanupTime.set(now)
            val thirtyDaysAgo = now - TimeUnit.DAYS.toMillis(CLEANUP_THRESHOLD_DAYS)
            dao.deleteOlderThan(thirtyDaysAgo)
        }
    }
}
