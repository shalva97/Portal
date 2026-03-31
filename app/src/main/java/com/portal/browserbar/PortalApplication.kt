package com.portal.browserbar

import android.app.Application
import androidx.room.Room
import com.portal.browserbar.data.local.AppDao
import com.portal.browserbar.data.local.AppDatabase
import com.portal.browserbar.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PortalApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

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

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            if (!repository.isInitialRefreshDone()) {
                repository.refreshApps()
            }
        }
    }
}
