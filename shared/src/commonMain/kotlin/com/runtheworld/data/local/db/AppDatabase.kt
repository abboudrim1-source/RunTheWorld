package com.runtheworld.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RunEntity::class,
        TerritoryEntity::class,
        UserAccountEntity::class,
        UserProfileEntity::class,
        FriendRequestEntity::class
    ],
    version = 5,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun runDao(): RunDao
    abstract fun territoryDao(): TerritoryDao
    abstract fun userAccountDao(): UserAccountDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun friendRequestDao(): FriendRequestDao

    companion object {
        const val DATABASE_NAME = "runtheworld.db"
    }
}
