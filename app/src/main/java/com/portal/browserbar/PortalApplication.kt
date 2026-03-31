package com.portal.browserbar

import android.app.Application
import androidx.room.Room
import com.portal.browserbar.data.local.AppDao
import com.portal.browserbar.data.local.AppDatabase
import com.portal.browserbar.data.repository.AppRepository

class PortalApplication : Application() {

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "portal_db"
        ).fallbackToDestructiveMigration(true).build()
    }

    val appDao: AppDao by lazy {
        database.appDao()
    }

    val repository: AppRepository by lazy {
        AppRepository(appDao, this)
    }
}
