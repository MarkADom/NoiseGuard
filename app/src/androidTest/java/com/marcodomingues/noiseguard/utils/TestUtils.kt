package com.marcodomingues.noiseguard.utils

import android.content.Context
import androidx.room.Room
import com.marcodomingues.noiseguard.data.local.NoiseLevelEntity
import com.marcodomingues.noiseguard.data.local.NoiseGuardDatabase

const val BASE_TIMESTAMP = 1_700_000_000_000L

fun hoursAgo(n: Int): Long = BASE_TIMESTAMP - n * 3_600_000L

fun createTestReading(
    decibels: Double,
    timestamp: Long,
    category: String = "QUIET"
): NoiseLevelEntity = NoiseLevelEntity(
    decibels = decibels,
    timestamp = timestamp,
    category = category
)

fun createInMemoryDatabase(context: Context): NoiseGuardDatabase =
    Room.inMemoryDatabaseBuilder(context, NoiseGuardDatabase::class.java)
        .allowMainThreadQueries()
        .build()
