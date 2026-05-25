package com.marcodomingues.noiseguard.di

import android.content.Context
import com.marcodomingues.noiseguard.data.audio.AudioAnalyzer
import com.marcodomingues.noiseguard.data.local.NoiseLevelDao
import com.marcodomingues.noiseguard.data.local.NoiseGuardDatabase
import com.marcodomingues.noiseguard.data.notification.NotificationHelper
import com.marcodomingues.noiseguard.data.preferences.UserPreferences
import com.marcodomingues.noiseguard.data.repository.NoiseRepositoryImpl
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoiseGuardDatabase(@ApplicationContext context: Context): NoiseGuardDatabase {
        return NoiseGuardDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideNoiseLevelDao(database: NoiseGuardDatabase): NoiseLevelDao {
        return database.noiseLevelDao()
    }

    @Provides
    @Singleton
    fun provideNoiseRepository(dao: NoiseLevelDao): NoiseRepository {
        return NoiseRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    fun provideAudioAnalyzer(): AudioAnalyzer {
        return AudioAnalyzer()
    }
}
