package com.portal.browserbar

import android.app.Application
import com.portal.browserbar.di.DataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

class PortalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger()
            androidContext(this@PortalApplication)
            modules(DataModule().module)
        }
    }
}
