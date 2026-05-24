package com.marcodomingues.noiseguard.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.marcodomingues.noiseguard.domain.model.NoiseLevel
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: NoiseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistoricalData()
    }

    private fun loadHistoricalData() {
        viewModelScope.launch {
            val oneDayAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1)

            repository.getReadingsSince(timestamp = oneDayAgo).collect { readings ->
                val newState = if (readings.isEmpty()) {
                    HistoryUiState(
                        last24HoursReadings = emptyList(),
                        averageToday = 0.0,
                        morningAverage = 0.0,
                        morningPeak = 0.0,
                        morningPeakTime = "--:--",
                        afternoonAverage = 0.0,
                        afternoonPeak = 0.0,
                        afternoonPeakTime = "--:--",
                        nightAverage = 0.0,
                        nightPeak = 0.0,
                        nightPeakTime = "--:--"
                    )
                } else {
                    HistoryUiState(
                        last24HoursReadings = readings,
                        averageToday = readings.map { it.decibels }.average(),
                        morningAverage = calculatePeriodAverage(readings, 6, 12),
                        morningPeak = calculatePeriodPeak(readings, 6, 12),
                        morningPeakTime = calculatePeriodPeakTime(readings, 6, 12),
                        afternoonAverage = calculatePeriodAverage(readings, 12, 18),
                        afternoonPeak = calculatePeriodPeak(readings, 12, 18),
                        afternoonPeakTime = calculatePeriodPeakTime(readings, 12, 18),
                        nightAverage = calculatePeriodAverage(readings, 18, 6),
                        nightPeak = calculatePeriodPeak(readings, 18, 6),
                        nightPeakTime = calculatePeriodPeakTime(readings, 18, 6)
                    )
                }
                _uiState.value = newState
            }
        }
    }

    private fun calculatePeriodAverage(readings: List<NoiseLevel>, startHour: Int, endHour: Int): Double {
        val filtered = filterByPeriod(readings, startHour, endHour)
        return if (filtered.isEmpty()) 0.0 else filtered.map { it.decibels }.average()
    }

    private fun calculatePeriodPeak(readings: List<NoiseLevel>, startHour: Int, endHour: Int): Double {
        return filterByPeriod(readings, startHour, endHour).maxOfOrNull { it.decibels } ?: 0.0
    }

    private fun calculatePeriodPeakTime(readings: List<NoiseLevel>, startHour: Int, endHour: Int): String {
        val peak = filterByPeriod(readings, startHour, endHour).maxByOrNull { it.decibels } ?: return "--:--"
        val calendar = Calendar.getInstance().apply { timeInMillis = peak.timestamp }
        return String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
    }
}

data class HistoryUiState(
    val last24HoursReadings: List<NoiseLevel> = emptyList(),
    val averageToday: Double = 0.0,
    val morningAverage: Double = 0.0,
    val morningPeak: Double = 0.0,
    val morningPeakTime: String = "--:--",
    val afternoonAverage: Double = 0.0,
    val afternoonPeak: Double = 0.0,
    val afternoonPeakTime: String = "--:--",
    val nightAverage: Double = 0.0,
    val nightPeak: Double = 0.0,
    val nightPeakTime: String = "--:--"
)

// Top-level so it's testable without instantiating the ViewModel.
// Uses Calendar.getInstance() to match the local timezone used by calculatePeriodPeakTime.
internal fun filterByPeriod(readings: List<NoiseLevel>, startHour: Int, endHour: Int): List<NoiseLevel> {
    return readings.filter { reading ->
        val hour = Calendar.getInstance().apply {
            timeInMillis = reading.timestamp
        }.get(Calendar.HOUR_OF_DAY)
        if (startHour < endHour) {
            hour in startHour until endHour
        } else {
            hour >= startHour || hour < endHour
        }
    }
}
