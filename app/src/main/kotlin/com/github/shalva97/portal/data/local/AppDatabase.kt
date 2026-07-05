package com.github.shalva97.portal.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AppEntity::class], version = STORAGE_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}

const val STORAGE_VERSION = 12