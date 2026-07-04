package com.github.shalva97.portal

import android.app.Application
import androidx.room.Room
import com.github.shalva97.portal.data.local.AppDao
import com.github.shalva97.portal.data.local.AppDatabase
import com.github.shalva97.portal.data.local.IconStorage
import com.github.shalva97.portal.data.repository.AppRepository
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
        ).addMigrations(AppDatabase.MIGRATION_3_4).fallbackToDestructiveMigration(true).build()
    }

    val appDao: AppDao by lazy {
        database.appDao()
    }
    
    val iconStorage: IconStorage by lazy {
        IconStorage(this)
    }

    val repository: AppRepository by lazy {
        AppRepository(appDao, this, iconStorage)
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
