package com.marcodomingues.noiseguard.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * The timestamp index makes range queries (last 24h, last hour) O(log n)
 * instead of full table scans.
 */
@Entity(
    tableName = "noise_readings",
    indices = [Index(value = ["timestamp"])]
)
data class NoiseLevelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val decibels: Double,

    val timestamp: Long,

    val category: String  // QUIET, MODERATE, LOUD, HARMFUL
)
