package com.marcodomingues.noiseguard.domain.model

data class NoiseLevel(
    val decibels: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val category: NoiseCategory
) {
    constructor(decibels: Double, timestamp: Long = System.currentTimeMillis()) : this(
        decibels = decibels,
        timestamp = timestamp,
        category = NoiseCategory.fromDecibels(decibels)
    )
}

enum class NoiseCategory {
    QUIET,
    MODERATE,
    LOUD,
    HARMFUL;

    companion object {
        fun fromDecibels(db: Double): NoiseCategory {
            return when {
                db < 40 -> QUIET
                db < 60 -> MODERATE
                db < 80 -> LOUD
                else -> HARMFUL
            }
        }
    }
}
