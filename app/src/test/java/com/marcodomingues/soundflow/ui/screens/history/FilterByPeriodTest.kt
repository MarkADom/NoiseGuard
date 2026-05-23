package com.marcodomingues.noiseguard.ui.screens.history

import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

// filterByPeriod uses Calendar.getInstance() (local timezone) for hour extraction,
// so test timestamps are constructed with Calendar too — consistent on any timezone.
class FilterByPeriodTest {

    private fun readingAtLocalHour(hour: Int, decibels: Double = 65.0): NoiseLevel {
        val timestamp = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        return NoiseLevel(decibels, timestamp)
    }

    private fun allHours(decibels: Double = 65.0) = (0..23).map { readingAtLocalHour(it, decibels) }

    // --- Morning (6–12) ---

    @Test
    fun `morning captures hours 6 through 11`() {
        assertEquals(6, filterByPeriod(allHours(), 6, 12).size)
    }

    @Test
    fun `hour 5 is not morning`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(5)), 6, 12).size)
    }

    @Test
    fun `hour 6 is morning`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(6)), 6, 12).size)
    }

    @Test
    fun `hour 11 is morning`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(11)), 6, 12).size)
    }

    @Test
    fun `hour 12 is not morning`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(12)), 6, 12).size)
    }

    // --- Afternoon (12–18) ---

    @Test
    fun `afternoon captures hours 12 through 17`() {
        assertEquals(6, filterByPeriod(allHours(), 12, 18).size)
    }

    @Test
    fun `hour 11 is not afternoon`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(11)), 12, 18).size)
    }

    @Test
    fun `hour 12 is afternoon`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(12)), 12, 18).size)
    }

    @Test
    fun `hour 18 is not afternoon`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(18)), 12, 18).size)
    }

    // --- Night (18–6, wraps midnight) ---

    @Test
    fun `night captures 18 to 23 and 0 to 5 - 12 hours total`() {
        assertEquals(12, filterByPeriod(allHours(), 18, 6).size)
    }

    @Test
    fun `hour 17 is not night`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(17)), 18, 6).size)
    }

    @Test
    fun `hour 18 is night`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(18)), 18, 6).size)
    }

    @Test
    fun `hour 23 is night`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(23)), 18, 6).size)
    }

    @Test
    fun `hour 0 is night`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(0)), 18, 6).size)
    }

    @Test
    fun `hour 5 is night`() {
        assertEquals(1, filterByPeriod(listOf(readingAtLocalHour(5)), 18, 6).size)
    }

    @Test
    fun `hour 6 is not night`() {
        assertEquals(0, filterByPeriod(listOf(readingAtLocalHour(6)), 18, 6).size)
    }

    // --- Edge cases ---

    @Test
    fun `empty list returns empty`() {
        assertTrue(filterByPeriod(emptyList(), 6, 12).isEmpty())
    }

    @Test
    fun `three periods together cover all 24 hours`() {
        val readings = allHours()
        val total = filterByPeriod(readings, 6, 12).size +
                filterByPeriod(readings, 12, 18).size +
                filterByPeriod(readings, 18, 6).size
        assertEquals(24, total)
    }
}
