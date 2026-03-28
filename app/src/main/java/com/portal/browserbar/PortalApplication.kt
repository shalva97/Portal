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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var database: AppDatabase
        private set

    lateinit var appDao: AppDao
        private set

    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()

        applicationScope.launch(Dispatchers.IO) {
            database = Room.databaseBuilder(
                this@PortalApplication,
                AppDatabase::class.java,
                "portal_db"
            ).fallbackToDestructiveMigration().build()

            appDao = database.appDao()
            repository = AppRepository(appDao, this@PortalApplication)
        }
    }
}
