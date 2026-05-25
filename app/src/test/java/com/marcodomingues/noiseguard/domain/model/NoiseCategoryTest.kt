package com.marcodomingues.noiseguard.domain.model

import com.marcodomingues.noiseguard.domain.model.NoiseCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class NoiseCategoryTest {

    // Boundaries: QUIET < 40, MODERATE 40–59, LOUD 60–79, HARMFUL 80+

    @Test
    fun `below 40 is QUIET`() {
        assertEquals(NoiseCategory.QUIET, NoiseCategory.fromDecibels(20.0))
        assertEquals(NoiseCategory.QUIET, NoiseCategory.fromDecibels(39.9))
        assertEquals(NoiseCategory.QUIET, NoiseCategory.fromDecibels(0.0))
    }

    @Test
    fun `exactly 40 is MODERATE not QUIET`() {
        assertEquals(NoiseCategory.MODERATE, NoiseCategory.fromDecibels(40.0))
    }

    @Test
    fun `mid-range 40 to 59 is MODERATE`() {
        assertEquals(NoiseCategory.MODERATE, NoiseCategory.fromDecibels(50.0))
        assertEquals(NoiseCategory.MODERATE, NoiseCategory.fromDecibels(59.9))
    }

    @Test
    fun `exactly 60 is LOUD not MODERATE`() {
        assertEquals(NoiseCategory.LOUD, NoiseCategory.fromDecibels(60.0))
    }

    @Test
    fun `mid-range 60 to 79 is LOUD`() {
        assertEquals(NoiseCategory.LOUD, NoiseCategory.fromDecibels(70.0))
        assertEquals(NoiseCategory.LOUD, NoiseCategory.fromDecibels(79.9))
    }

    @Test
    fun `exactly 80 is HARMFUL not LOUD`() {
        assertEquals(NoiseCategory.HARMFUL, NoiseCategory.fromDecibels(80.0))
    }

    @Test
    fun `above 80 is HARMFUL`() {
        assertEquals(NoiseCategory.HARMFUL, NoiseCategory.fromDecibels(90.0))
        assertEquals(NoiseCategory.HARMFUL, NoiseCategory.fromDecibels(120.0))
    }

    @Test
    fun `negative dB is QUIET - AudioAnalyzer clamps to MIN_DB but fromDecibels handles it gracefully`() {
        assertEquals(NoiseCategory.QUIET, NoiseCategory.fromDecibels(-10.0))
    }
}
