package com.marcodomingues.noiseguard.data.preferences

import com.marcodomingues.noiseguard.data.preferences.UserPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserPreferencesValidationTest {

    // --- isValidThreshold ---

    @Test
    fun `threshold 50 is valid lower bound`() {
        assertTrue(UserPreferences.isValidThreshold(50))
    }

    @Test
    fun `threshold 120 is valid upper bound`() {
        assertTrue(UserPreferences.isValidThreshold(120))
    }

    @Test
    fun `threshold 70 is valid middle value`() {
        assertTrue(UserPreferences.isValidThreshold(70))
    }

    @Test
    fun `threshold 49 is invalid`() {
        assertFalse(UserPreferences.isValidThreshold(49))
    }

    @Test
    fun `threshold 121 is invalid`() {
        assertFalse(UserPreferences.isValidThreshold(121))
    }

    @Test
    fun `threshold 0 is invalid`() {
        assertFalse(UserPreferences.isValidThreshold(0))
    }

    @Test
    fun `threshold negative is invalid`() {
        assertFalse(UserPreferences.isValidThreshold(-1))
    }

    // --- isValidInterval ---

    @Test
    fun `interval 50 is valid`() {
        assertTrue(UserPreferences.isValidInterval(50))
    }

    @Test
    fun `interval 100 is valid`() {
        assertTrue(UserPreferences.isValidInterval(100))
    }

    @Test
    fun `interval 500 is valid`() {
        assertTrue(UserPreferences.isValidInterval(500))
    }

    @Test
    fun `interval 99 is invalid - only exact values allowed`() {
        assertFalse(UserPreferences.isValidInterval(99))
    }

    @Test
    fun `interval 0 is invalid`() {
        assertFalse(UserPreferences.isValidInterval(0))
    }

    @Test
    fun `interval 200 is invalid`() {
        assertFalse(UserPreferences.isValidInterval(200))
    }
}
