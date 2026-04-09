package com.runtheworld.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RunEntity::class, TerritoryEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
    abstract fun territoryDao(): TerritoryDao

    companion object {
        const val DATABASE_NAME = "runtheworld.db"
    }
}
