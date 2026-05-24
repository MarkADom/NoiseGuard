package com.marcodomingues.noiseguard.domain.model

import com.marcodomingues.noiseguard.domain.model.NoiseCategory
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class NoiseLevelTest {

    @Test
    fun `secondary constructor derives category from decibels`() {
        assertEquals(NoiseCategory.QUIET, NoiseLevel(30.0).category)
        assertEquals(NoiseCategory.MODERATE, NoiseLevel(50.0).category)
        assertEquals(NoiseCategory.LOUD, NoiseLevel(70.0).category)
        assertEquals(NoiseCategory.HARMFUL, NoiseLevel(90.0).category)
    }

    @Test
    fun `category at 40 dB boundary is MODERATE via secondary constructor`() {
        // Confirms the secondary constructor delegation — if someone broke the
        // constructor chain, this catches it even if NoiseCategoryTest still passes.
        assertEquals(NoiseCategory.MODERATE, NoiseLevel(40.0).category)
        assertEquals(NoiseCategory.QUIET, NoiseLevel(39.9).category)
    }
}
