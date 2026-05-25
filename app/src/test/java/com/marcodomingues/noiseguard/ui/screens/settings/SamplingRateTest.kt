package com.marcodomingues.noiseguard.ui.screens.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SamplingRateTest {

    // --- fromLabel ---

    @Test
    fun `fromLabel fast returns FAST`() {
        assertEquals(SamplingRate.FAST, SamplingRate.fromLabel("fast"))
    }

    @Test
    fun `fromLabel normal returns NORMAL`() {
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromLabel("normal"))
    }

    @Test
    fun `fromLabel battery returns BATTERY`() {
        assertEquals(SamplingRate.BATTERY, SamplingRate.fromLabel("battery"))
    }

    @Test
    fun `fromLabel unknown string falls back to NORMAL`() {
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromLabel(""))
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromLabel("Fast")) // case-sensitive
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromLabel("something_else"))
    }

    // --- fromInterval ---

    @Test
    fun `fromInterval 50 returns FAST`() {
        assertEquals(SamplingRate.FAST, SamplingRate.fromInterval(50))
    }

    @Test
    fun `fromInterval 100 returns NORMAL`() {
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromInterval(100))
    }

    @Test
    fun `fromInterval 500 returns BATTERY`() {
        assertEquals(SamplingRate.BATTERY, SamplingRate.fromInterval(500))
    }

    @Test
    fun `fromInterval unknown value falls back to NORMAL`() {
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromInterval(0))
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromInterval(99))
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromInterval(999))
    }

    // --- Round-trips ---

    @Test
    fun `FAST label round-trips through fromLabel`() {
        assertEquals(SamplingRate.FAST, SamplingRate.fromLabel(SamplingRate.FAST.label))
    }

    @Test
    fun `NORMAL intervalMs round-trips through fromInterval`() {
        assertEquals(SamplingRate.NORMAL, SamplingRate.fromInterval(SamplingRate.NORMAL.intervalMs))
    }

    @Test
    fun `BATTERY intervalMs round-trips through fromInterval`() {
        assertEquals(SamplingRate.BATTERY, SamplingRate.fromInterval(SamplingRate.BATTERY.intervalMs))
    }
}
