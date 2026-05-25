package com.marcodomingues.noiseguard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Schema version 1. Migrations must be written manually before any schema change —
 * fallbackToDestructiveMigration is disabled, so a missing migration throws at startup.
 */
@Database(
    entities = [NoiseLevelEntity::class],
    version = 1,
    exportSchema = true
)
abstract class NoiseGuardDatabase : RoomDatabase() {

    abstract fun noiseLevelDao(): NoiseLevelDao

    companion object {
        @Volatile
        private var INSTANCE: NoiseGuardDatabase? = null

        fun getDatabase(context: Context): NoiseGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoiseGuardDatabase::class.java,
                    "noiseguard_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
