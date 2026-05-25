package com.marcodomingues.noiseguard.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcodomingues.noiseguard.data.preferences.UserPreferences
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

enum class SamplingRate(val label: String, val intervalMs: Int) {
    FAST("fast", 50),
    NORMAL("normal", 100),
    BATTERY("battery", 500);

    companion object {
        fun fromLabel(label: String) = values().find { it.label == label } ?: NORMAL
        fun fromInterval(intervalMs: Int) = values().find { it.intervalMs == intervalMs } ?: NORMAL
    }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val repository: NoiseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferences.settingsFlow.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    noiseLimit = settings.alertThreshold.toFloat(),
                    pushNotificationsEnabled = settings.enableNotifications,
                    vibrationEnabled = settings.enableVibration,
                    samplingRate = SamplingRate.fromInterval(settings.monitoringInterval).label,
                    autoStartEnabled = settings.autoStart,
                    forceDarkMode = settings.forceDarkMode
                )
            }
        }
    }

    fun updateNoiseLimit(limit: Float) {
        viewModelScope.launch {
            try {
                userPreferences.setAlertThreshold(limit.toInt())
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun togglePushNotifications(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setEnableNotifications(enabled)
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun toggleVibration(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setEnableVibration(enabled)
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun updateSamplingRate(rate: String) {
        viewModelScope.launch {
            try {
                userPreferences.setMonitoringInterval(SamplingRate.fromLabel(rate).intervalMs)
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun toggleAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setAutoStart(enabled)
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun toggleForceDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferences.setForceDarkMode(enabled)
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Failed to save setting") }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.deleteAllReadings()
            _uiState.value = _uiState.value.copy(historyCleared = true)
        }
    }

    fun onHistoryClearedShown() {
        _uiState.value = _uiState.value.copy(historyCleared = false)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class SettingsUiState(
    val noiseLimit: Float = 70f,
    val pushNotificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val samplingRate: String = "normal",
    val autoStartEnabled: Boolean = false,
    val forceDarkMode: Boolean = false,
    val historyCleared: Boolean = false,
    val errorMessage: String? = null
)
