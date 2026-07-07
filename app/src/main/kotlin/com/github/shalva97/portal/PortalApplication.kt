package com.github.shalva97.portal

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import com.github.shalva97.portal.data.local.AppStorage
import com.github.shalva97.portal.data.local.IconStorage
import com.github.shalva97.portal.data.repository.AppRepository
import com.github.shalva97.portal.receiver.PackageChangeReceiver
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
        registerReceiver(
            PackageChangeReceiver(),
            IntentFilter().apply {
                addAction(Intent.ACTION_PACKAGE_ADDED)
                addAction(Intent.ACTION_PACKAGE_REMOVED)
                addAction(Intent.ACTION_PACKAGE_CHANGED)
                addAction(Intent.ACTION_PACKAGE_REPLACED)
                addDataScheme("package")
            }
        )
        applicationScope.launch(Dispatchers.IO) {
            if (!storage.isInitialRefreshDone()) {
                repository.refreshApps()
                storage.markInitialRefreshDone()
            }
        }
    }
}
