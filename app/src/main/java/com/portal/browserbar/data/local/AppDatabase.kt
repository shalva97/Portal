package com.portal.browserbar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
