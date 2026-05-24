package com.marcodomingues.noiseguard.data.audio

import com.marcodomingues.noiseguard.data.audio.AudioAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// calculateDecibels is internal — accessible from the test source set of the same module.
// Formula: rawDb = 20 * log10(rms / 32768) + 80.0, clamped to [20.0, 120.0]
//
// NOTE: README says "82 dB hardware offset" but the code uses CALIBRATION_OFFSET = 80.0.
// These tests pin the actual code behaviour (80.0). If the offset is intentionally 82,
// update CALIBRATION_OFFSET and the expected values below.
class AudioAnalyzerDecibelsTest {

    private val analyzer = AudioAnalyzer()

    private fun uniformBuffer(amplitude: Int, size: Int = 1024): ShortArray =
        ShortArray(size) { amplitude.toShort() }

    @Test
    fun `zero amplitude returns minimum dB floor`() {
        val buffer = ShortArray(1024) { 0 }
        assertEquals(20.0, analyzer.calculateDecibels(buffer, 1024), 0.001)
    }

    @Test
    fun `10 percent of full amplitude gives approximately 60 dB`() {
        // rms ≈ 3276 → 20 * log10(3276/32768) + 80 ≈ 60 dB
        val amplitude = (32768 * 0.1).toInt()
        val result = analyzer.calculateDecibels(uniformBuffer(amplitude), 1024)
        assertEquals(60.0, result, 0.5)
    }

    @Test
    fun `1 percent of full amplitude gives approximately 40 dB`() {
        // rms ≈ 327 → 20 * log10(327/32768) + 80 ≈ 40 dB
        val amplitude = (32768 * 0.01).toInt()
        val result = analyzer.calculateDecibels(uniformBuffer(amplitude), 1024)
        assertEquals(40.0, result, 0.5)
    }

    @Test
    fun `very low amplitude is clamped to 20 dB minimum`() {
        // amplitude=1 → rawDb ≈ -10 dB, clamped to 20
        val result = analyzer.calculateDecibels(uniformBuffer(1), 1024)
        assertEquals(20.0, result, 0.001)
    }

    @Test
    fun `near-full amplitude does not exceed 120 dB ceiling`() {
        val result = analyzer.calculateDecibels(uniformBuffer(Short.MAX_VALUE.toInt()), 1024)
        assertTrue("Expected <= 120 dB, got $result", result <= 120.0)
    }

    @Test
    fun `output is always within valid range for all representative amplitudes`() {
        val amplitudes = listOf(0, 1, 32, 327, 1000, 3276, 10000, 32767)
        for (amplitude in amplitudes) {
            val db = analyzer.calculateDecibels(uniformBuffer(amplitude), 1024)
            assertTrue(
                "amplitude=$amplitude produced $db dB, outside [20, 120]",
                db in 20.0..120.0
            )
        }
    }

    @Test
    fun `mixed positive and negative samples produce the same RMS as uniform positive`() {
        // RMS squares samples, so sign doesn't affect the result
        val positive = uniformBuffer(1000, 1024)
        val alternating = ShortArray(1024) { i -> if (i % 2 == 0) 1000.toShort() else (-1000).toShort() }
        val dbPositive = analyzer.calculateDecibels(positive, 1024)
        val dbAlternating = analyzer.calculateDecibels(alternating, 1024)
        assertEquals(dbPositive, dbAlternating, 0.001)
    }
}
