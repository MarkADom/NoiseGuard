package com.marcodomingues.noiseguard.data.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.math.log10
import kotlin.math.sqrt

/**
 * Reads raw PCM audio from the microphone and emits calibrated dB SPL readings.
 * Formula: 20 * log10(rms / 32768) + 80.0, clamped to [20, 120] dB.
 */
class AudioAnalyzer {
    @Volatile private var audioRecord: AudioRecord? = null

    companion object {
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        private const val REFERENCE_AMPLITUDE = 32768.0
        private const val CALIBRATION_OFFSET = 80.0
        private const val MIN_DB = 20.0
        private const val MAX_DB = 120.0
    }

    /**
     * Starts monitoring and emits dB SPL readings as a Flow.
     *
     * @param intervalMs Milliseconds between readings. Default 100ms.
     * @return Flow of dB values in the range [20.0, 120.0].
     */
    // Caller is responsible for requesting RECORD_AUDIO before calling this.
    @SuppressLint("MissingPermission")
    fun startMonitoring(intervalMs: Long = 100): Flow<Double> = flow {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        ) * 2  // Double buffer for stability

        val newRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize
        )
        audioRecord = newRecord  // assign before checking so finally can release it
        if (newRecord.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("AudioRecord failed to initialize")
        }
        newRecord.startRecording()

        val buffer = ShortArray(bufferSize)

        try {
            while (coroutineContext.isActive) {
                val read = audioRecord?.read(buffer, 0, bufferSize) ?: 0
                if (read > 0) {
                    val decibels = calculateDecibels(buffer, read)
                    emit(decibels)
                }
                delay(intervalMs)
            }
        } finally {
            stopMonitoring()
        }
    }.flowOn(Dispatchers.Default)

    internal fun calculateDecibels(buffer: ShortArray, size: Int): Double {
        var sum = 0.0
        for (i in 0 until size) {
            val sample = buffer[i].toDouble()
            sum += sample * sample
        }
        val rms = sqrt(sum / size)

        return if (rms > 0.0) {
            val rawDb = 20 * log10(rms / REFERENCE_AMPLITUDE) + CALIBRATION_OFFSET
            rawDb.coerceIn(MIN_DB, MAX_DB)
        } else {
            MIN_DB
        }
    }

    /**
     * Stops recording and releases the AudioRecord. Safe to call multiple times.
     */
    fun stopMonitoring() {
        try {
            audioRecord?.apply {
                if (recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                    stop()
                }
                if (state == AudioRecord.STATE_INITIALIZED) {
                    release()
                }
            }
        } catch (e: IllegalStateException) {
            // Already stopped or released — safe to ignore
        } finally {
            audioRecord = null
        }
    }
}
