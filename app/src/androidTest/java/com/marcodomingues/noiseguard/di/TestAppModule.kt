package com.marcodomingues.noiseguard.di

import android.content.Context
import androidx.room.Room
import com.marcodomingues.noiseguard.data.audio.AudioAnalyzer
import com.marcodomingues.noiseguard.data.local.NoiseLevelDao
import com.marcodomingues.noiseguard.data.local.NoiseGuardDatabase
import com.marcodomingues.noiseguard.data.notification.NotificationHelper
import com.marcodomingues.noiseguard.data.preferences.UserPreferences
import com.marcodomingues.noiseguard.data.repository.NoiseRepositoryImpl
import com.marcodomingues.noiseguard.domain.repository.NoiseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideInMemoryDatabase(@ApplicationContext context: Context): NoiseGuardDatabase =
        Room.inMemoryDatabaseBuilder(context, NoiseGuardDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    @Singleton
    fun provideNoiseLevelDao(database: NoiseGuardDatabase): NoiseLevelDao =
        database.noiseLevelDao()

    @Provides
    @Singleton
    fun provideNoiseRepository(dao: NoiseLevelDao): NoiseRepository =
        NoiseRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper =
        NotificationHelper(context)

    @Provides
    fun provideAudioAnalyzer(): AudioAnalyzer = AudioAnalyzer()
}
