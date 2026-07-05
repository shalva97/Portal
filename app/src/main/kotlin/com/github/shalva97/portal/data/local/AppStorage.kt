package com.github.shalva97.portal.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class AppStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        if (prefs.getInt(KEY_STORAGE_VERSION, 0) != STORAGE_VERSION) {
            prefs.edit(commit = true) {
                clear()
                putInt(KEY_STORAGE_VERSION, STORAGE_VERSION)
            }
        }
    }

    val database: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DB_NAME
    )
        .fallbackToDestructiveMigration(true)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                prefs.edit(commit = true) { remove(KEY_INITIAL_REFRESH_DONE) }
            }
        })
        .build()

    val appDao: AppDao = database.appDao()

    fun isInitialRefreshDone(): Boolean = prefs.getBoolean(KEY_INITIAL_REFRESH_DONE, false)

    fun markInitialRefreshDone() {
        prefs.edit { putBoolean(KEY_INITIAL_REFRESH_DONE, true) }
    }

    companion object {
        private const val PREFS_NAME = "app_prefs"
        private const val DB_NAME = "portal_db"
        private const val KEY_STORAGE_VERSION = "storage_version"
        private const val KEY_INITIAL_REFRESH_DONE = "initial_refresh_done"
    }
}
