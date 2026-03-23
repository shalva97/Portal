package com.portal.browserbar

import android.app.Application
import androidx.room.Room
import com.portal.browserbar.data.local.AppDao
import com.portal.browserbar.data.local.AppDatabase
import com.portal.browserbar.data.repository.AppRepository

class PortalApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var appDao: AppDao
        private set

    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "portal_db"
        ).fallbackToDestructiveMigration().build()

        appDao = database.appDao()
        repository = AppRepository(appDao, this)
    }
}
