package com.marcodomingues.noiseguard.ui.screens.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcodomingues.noiseguard.data.audio.AudioAnalyzer
import com.marcodomingues.noiseguard.data.notification.NotificationHelper
import com.marcodomingues.noiseguard.data.preferences.Settings
import com.marcodomingues.noiseguard.data.preferences.UserPreferences
import com.marcodomingues.noiseguard.domain.model.NoiseCategory
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject

/**
 * Drives the Monitor screen. Owns three coroutine jobs:
 * - monitoringJob: audio collection loop
 * - statsJob: polls Room every 10s for peak/avg stats
 * - settingsJob: keeps currentSettings in sync with DataStore
 */
@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val repository: NoiseRepository,
    private val userPreferences: UserPreferences,
    private val notificationHelper: NotificationHelper,
    private val audioAnalyzer: AudioAnalyzer
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitorUiState())
    val uiState: StateFlow<MonitorUiState> = _uiState.asStateFlow()

    private var monitoringJob: Job? = null
    private var statsJob: Job? = null
    private var settingsJob: Job? = null

    // Minimum gap between successive notifications to prevent spam
    private val lastNotificationTime = AtomicLong(0L)
    private val NOTIFICATION_COOLDOWN_MS = 30_000L

    // Latest settings snapshot — written and read on Main dispatcher (viewModelScope)
    private var currentSettings: Settings? = null

    init {
        settingsJob = viewModelScope.launch {
            userPreferences.settingsFlow.collect { settings ->
                currentSettings = settings
            }
        }
    }

    fun startMonitoring() {
        if (monitoringJob?.isActive == true) return

        monitoringJob = viewModelScope.launch {
            try {
                audioAnalyzer.startMonitoring(intervalMs = 100).collect { decibels ->
                    val noiseLevel = NoiseLevel(decibels)

                    _uiState.update { state ->
                        state.copy(
                            currentDecibels = decibels,
                            category = noiseLevel.category,
                            isMonitoring = true,
                            recentReadings = (state.recentReadings + decibels).takeLast(10)
                        )
                    }

                    repository.saveReading(noiseLevel)

                    val settings = currentSettings
                    if (settings != null && settings.enableNotifications) {
                        if (decibels >= settings.alertThreshold) {
                            val now = System.currentTimeMillis()
                            if (now - lastNotificationTime.get() >= NOTIFICATION_COOLDOWN_MS) {
                                lastNotificationTime.set(now)
                                notificationHelper.sendNoiseAlert(decibels, settings.alertThreshold)
                                if (settings.enableVibration) {
                                    notificationHelper.vibrate()
                                }
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IllegalStateException) {
                _uiState.update {
                    it.copy(
                        isMonitoring = false,
                        errorMessage = "Microphone unavailable. Check app permissions."
                    )
                }
            }
        }

        statsJob = viewModelScope.launch {
            while (true) {
                updateStats()
                kotlinx.coroutines.delay(10_000)
            }
        }
    }

    fun stopMonitoring() {
        monitoringJob?.cancel()
        statsJob?.cancel()
        // audioAnalyzer lifecycle is owned by the Flow's finally block (via monitoringJob cancel)
        // and onCleared() as a safety net — do not call stopMonitoring() here directly

        viewModelScope.launch(NonCancellable) {
            repository.flushBuffer()
        }

        _uiState.update { it.copy(isMonitoring = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private suspend fun updateStats() {
        val now = System.currentTimeMillis()
        val oneDayAgo = now - TimeUnit.DAYS.toMillis(1)
        val oneHourAgo = now - TimeUnit.HOURS.toMillis(1)

        val peakToday = repository.getPeakSince(oneDayAgo) ?: 0.0
        val avgLastHour = repository.getAverageSince(oneHourAgo) ?: 0.0

        _uiState.update { state ->
            state.copy(
                peakToday = peakToday,
                averageLastHour = avgLastHour
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        settingsJob?.cancel()
        audioAnalyzer.stopMonitoring()
        viewModelScope.launch(NonCancellable) {
            repository.flushBuffer()
        }
    }
}

data class MonitorUiState(
    val currentDecibels: Double = 0.0,
    val category: NoiseCategory = NoiseCategory.QUIET,
    val isMonitoring: Boolean = false,
    val peakToday: Double = 0.0,
    val averageLastHour: Double = 0.0,
    val recentReadings: List<Double> = emptyList(),
    val errorMessage: String? = null
)
