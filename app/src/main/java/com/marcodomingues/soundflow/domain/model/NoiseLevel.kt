package com.marcodomingues.soundflow.domain.model

/**
 * Represents a noise level reading with automatic categorization.
 *
 * @property decibels The measured sound level in decibels (dB)
 * @property timestamp Unix timestamp in milliseconds when reading was taken
 */
data class NoiseLevel(
    val decibels: Double,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Auto-computed category based on decibel ranges:
     * - QUIET: 0-39 dB
     * - MODERATE: 40-59 dB
     * - LOUD: 60-79 dB
     * - HARMFUL: 80+ dB
     */
    val category: NoiseCategory
        get() = when {
            decibels < 40 -> NoiseCategory.QUIET
            decibels < 60 -> NoiseCategory.MODERATE
            decibels < 80 -> NoiseCategory.LOUD
            else -> NoiseCategory.HARMFUL
        }
}

/**
 * Noise level categories following WHO recommendations.
 * Labels are internal identifiers; UI translations handled via string resources.
 */
enum class NoiseCategory(val label: String) {
    QUIET("Quiet"),
    MODERATE("Moderate"),
    LOUD("Loud"),
    HARMFUL("Harmful")
}