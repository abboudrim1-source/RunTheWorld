package com.runtheworld.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.runtheworld.data.local.db.AppDatabase
import com.runtheworld.platform.AndroidLocationService
import com.runtheworld.platform.LocationService
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // Room database
    single<AppDatabase> {
        val migration5to6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE runs ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE runs ADD COLUMN ownerColorHex TEXT NOT NULL DEFAULT '#1A73E8'")
            }
        }
        Room.databaseBuilder(
            context = androidContext(),
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        )
            .addMigrations(migration5to6)
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs — sourced from the single AppDatabase instance
    single { get<AppDatabase>().runDao() }
    single { get<AppDatabase>().territoryDao() }
    single { get<AppDatabase>().userAccountDao() }
    single { get<AppDatabase>().userProfileDao() }
    single { get<AppDatabase>().friendRequestDao() }

    // Location
    single<LocationService> { AndroidLocationService(androidContext()) }

    // Settings (SharedPreferences under the hood)
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("rtw_prefs", android.content.Context.MODE_PRIVATE)
        )
    }
}
