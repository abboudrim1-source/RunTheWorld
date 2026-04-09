package com.runtheworld.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.runtheworld.data.local.db.AppDatabase
import com.runtheworld.data.local.db.Converters
import com.runtheworld.platform.IosLocationService
import com.runtheworld.platform.LocationService
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    // Room database using bundled SQLite (no system SQLite dependency on iOS)
    single<AppDatabase> {
        val dbPath = NSHomeDirectory() + "/Documents/${AppDatabase.DATABASE_NAME}"
        Room.databaseBuilder<AppDatabase>(name = dbPath)
            .addTypeConverter(Converters())
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().runDao() }
    single { get<AppDatabase>().territoryDao() }
    single { get<AppDatabase>().userAccountDao() }

    // Location
    single<LocationService> { IosLocationService() }

    // Settings (NSUserDefaults under the hood)
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}
