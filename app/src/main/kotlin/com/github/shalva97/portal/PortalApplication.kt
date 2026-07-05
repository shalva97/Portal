package com.github.shalva97.portal

import android.app.Application
import com.github.shalva97.portal.data.local.AppStorage
import com.github.shalva97.portal.data.local.IconStorage
import com.github.shalva97.portal.data.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PortalApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val storage: AppStorage by lazy { AppStorage(this) }

    val iconStorage: IconStorage by lazy { IconStorage(this) }

    val repository: AppRepository by lazy {
        AppRepository(storage.appDao, this, iconStorage)
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch(Dispatchers.IO) {
            if (!storage.isInitialRefreshDone()) {
                repository.refreshApps()
                storage.markInitialRefreshDone()
            }
        }
    }
}
