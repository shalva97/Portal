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
        val packageName = intent.data?.schemeSpecificPart

        if (packageName != null) {
            scope.launch {
                when (action) {
                    Intent.ACTION_PACKAGE_ADDED,
                    Intent.ACTION_PACKAGE_CHANGED,
                    Intent.ACTION_PACKAGE_REPLACED -> {
                        repository.refreshApp(packageName)
                    }
                    Intent.ACTION_PACKAGE_REMOVED -> {
                        // Check if it's a real removal or just an update in progress
                        val isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
                        if (!isReplacing) {
                            repository.removeApp(packageName)
                        }
                    }
                }
            }
        }
    }
}
