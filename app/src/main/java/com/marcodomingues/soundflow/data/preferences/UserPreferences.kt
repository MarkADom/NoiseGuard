package com.marcodomingues.noiseguard.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

        // Keys
        private val ALERT_THRESHOLD = intPreferencesKey("alert_threshold")
        private val MONITORING_INTERVAL = intPreferencesKey("monitoring_interval")
        private val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        private val ENABLE_VIBRATION = booleanPreferencesKey("enable_vibration")
        private val AUTO_START = booleanPreferencesKey("auto_start")
        private val FORCE_DARK_MODE = booleanPreferencesKey("force_dark_mode")

        // Defaults
        const val DEFAULT_THRESHOLD = 70
        const val DEFAULT_INTERVAL = 100

        // Extracted so they can be unit-tested without an Android context
        fun isValidThreshold(value: Int): Boolean = value in 50..120
        fun isValidInterval(value: Int): Boolean = value in listOf(50, 100, 500)
    }

    val settingsFlow: Flow<Settings> = context.dataStore.data.map { preferences ->
        Settings(
            alertThreshold = preferences[ALERT_THRESHOLD] ?: DEFAULT_THRESHOLD,
            monitoringInterval = preferences[MONITORING_INTERVAL] ?: DEFAULT_INTERVAL,
            enableNotifications = preferences[ENABLE_NOTIFICATIONS] ?: true,
            enableVibration = preferences[ENABLE_VIBRATION] ?: true,
            autoStart = preferences[AUTO_START] ?: false,
            forceDarkMode = preferences[FORCE_DARK_MODE] ?: false
        )
    }

    suspend fun setAlertThreshold(value: Int) {
        require(isValidThreshold(value)) { "Alert threshold must be between 50 and 120 dB" }
        context.dataStore.edit { preferences ->
            preferences[ALERT_THRESHOLD] = value
        }
    }

    suspend fun setMonitoringInterval(value: Int) {
        require(isValidInterval(value)) { "Monitoring interval must be 50, 100, or 500ms" }
        context.dataStore.edit { preferences ->
            preferences[MONITORING_INTERVAL] = value
        }
    }

    suspend fun setEnableNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_NOTIFICATIONS] = enabled
        }
    }

    suspend fun setEnableVibration(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ENABLE_VIBRATION] = enabled
        }
    }

    suspend fun setAutoStart(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_START] = enabled
        }
    }

    suspend fun setForceDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FORCE_DARK_MODE] = enabled
        }
    }
}

data class Settings(
    val alertThreshold: Int = UserPreferences.DEFAULT_THRESHOLD,
    val monitoringInterval: Int = UserPreferences.DEFAULT_INTERVAL,
    val enableNotifications: Boolean = true,
    val enableVibration: Boolean = true,
    val autoStart: Boolean = false,
    val forceDarkMode: Boolean = false
)
