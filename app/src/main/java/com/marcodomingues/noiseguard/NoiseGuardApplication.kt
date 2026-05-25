package com.marcodomingues.noiseguard

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

/**
 * Creates the notification channel on startup. The channel must exist before
 * NotificationHelper posts any notification — if this class isn't declared in
 * AndroidManifest.xml via android:name, alerts are silently dropped.
 */
@HiltAndroidApp
class NoiseGuardApplication : Application() {

    companion object {
        const val NOISE_CHANNEL_ID = "noiseguard_alerts"
        const val NOISE_CHANNEL_NAME = "Noise Alerts"
        const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOISE_CHANNEL_ID,
                NOISE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when noise exceeds safe levels"
                enableVibration(true)
                enableLights(true)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
