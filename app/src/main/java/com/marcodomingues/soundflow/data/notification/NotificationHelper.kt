package com.marcodomingues.noiseguard.data.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.marcodomingues.noiseguard.R
import com.marcodomingues.noiseguard.NoiseGuardApplication

/**
 * Posts noise alert notifications and triggers vibration.
 * Both require the channel created in NoiseGuardApplication.onCreate().
 */
class NotificationHelper(private val context: Context) {

    /**
     * Returns early without posting if POST_NOTIFICATIONS isn't granted (API 33+).
     */
    fun sendNoiseAlert(decibels: Double, threshold: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val notification = NotificationCompat.Builder(context, NoiseGuardApplication.NOISE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Noise Alert")
            .setContentText(
                "Current level ${decibels.toInt()} dB exceeds your $threshold dB limit"
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(NoiseGuardApplication.NOTIFICATION_ID, notification)
    }

    /**
     * Routes to the appropriate vibration API based on device API level.
     * VibratorManager (API 31+) → VibrationEffect (API 26-30) → legacy vibrate (API 24-25).
     */
    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator?.vibrate(
                VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            // Vibrator.getSystemService() is deprecated in favour of VibratorManager,
            // but VibratorManager only exists on API 31+.
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                // VibrationEffect requires API 26; on API 24-25 only the legacy long overload exists.
                @Suppress("DEPRECATION")
                vibrator?.vibrate(300L)
            }
        }
    }
}
