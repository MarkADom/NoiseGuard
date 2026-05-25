package com.marcodomingues.noiseguard.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoiseLevelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(readings: List<NoiseLevelEntity>)

    /**
     * @param startTime Unix timestamp in milliseconds.
     * @return Flow of readings ordered by timestamp descending. Re-emits on every insert.
     */
    @Query("SELECT * FROM noise_readings WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getReadingsSince(startTime: Long): Flow<List<NoiseLevelEntity>>

    /**
     * @return Average dB since [startTime], or null if no readings exist.
     */
    @Query("SELECT AVG(decibels) FROM noise_readings WHERE timestamp >= :startTime")
    suspend fun getAverageSince(startTime: Long): Double?

    /**
     * @return Peak dB since [startTime], or null if no readings exist.
     */
    @Query("SELECT MAX(decibels) FROM noise_readings WHERE timestamp >= :startTime")
    suspend fun getPeakSince(startTime: Long): Double?

    /**
     * @return Number of rows deleted.
     */
    @Query("DELETE FROM noise_readings WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long): Int

    @Query("DELETE FROM noise_readings")
    suspend fun deleteAll()
}
