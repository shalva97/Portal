package com.portal.browserbar.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.portal.browserbar.PortalApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PackageChangeReceiver : BroadcastReceiver() {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val repository = (context.applicationContext as PortalApplication).repository
        val action = intent.action
        if (action == Intent.ACTION_PACKAGE_ADDED || 
            action == Intent.ACTION_PACKAGE_REMOVED || 
            action == Intent.ACTION_PACKAGE_CHANGED ||
            action == Intent.ACTION_PACKAGE_REPLACED) {
            
            scope.launch {
                // Force refresh because we know something changed
                repository.refreshApps(force = true)
            }
        }
    }
}
