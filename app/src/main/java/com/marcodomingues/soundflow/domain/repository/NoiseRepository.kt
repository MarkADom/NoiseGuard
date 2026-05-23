package com.marcodomingues.noiseguard.domain.repository

import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import kotlinx.coroutines.flow.Flow

/**
 * Persistence contract for noise readings. Lives in the domain layer —
 * no Room or DataStore imports here.
 */
interface NoiseRepository {

    /**
     * Accepts a reading for persistence. Implementations may buffer internally;
     * call [flushBuffer] to guarantee all readings are written.
     */
    suspend fun saveReading(noiseLevel: NoiseLevel)

    /**
     * Writes any buffered readings immediately. Call this when monitoring stops
     * to prevent data loss.
     */
    suspend fun flushBuffer()

    /**
     * @return Flow of domain models since [timestamp] (ms). Re-emits on new data.
     */
    fun getReadingsSince(timestamp: Long): Flow<List<NoiseLevel>>

    /**
     * @return Average dB since [timestamp], or null if no readings exist.
     */
    suspend fun getAverageSince(timestamp: Long): Double?

    /**
     * @return Peak dB since [timestamp], or null if no readings exist.
     */
    suspend fun getPeakSince(timestamp: Long): Double?

    suspend fun deleteAllReadings()
}
