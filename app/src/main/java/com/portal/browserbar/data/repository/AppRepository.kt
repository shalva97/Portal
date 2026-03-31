package com.portal.browserbar.data.repository

import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import androidx.core.net.toUri
import com.portal.browserbar.data.local.AppDao
import com.portal.browserbar.data.local.AppEntity
import com.portal.browserbar.domain.model.AppModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppRepository(
    private val appDao: AppDao,
    private val context: Context
) {
    private val packageManager = context.packageManager
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_INITIAL_REFRESH_DONE = "initial_refresh_done"
    }

    fun getVisibleApps(): Flow<List<AppModel>> = appDao.getVisibleApps().map { entities ->
        entities.map { it.toModel() }
    }

    fun getAllApps(): Flow<List<AppModel>> = appDao.getAllApps().map { entities ->
        entities.map { it.toModel() }
    }

    fun getRecentlyUsedApps(): Flow<List<AppModel>> = appDao.getRecentlyUsedApps().map { entities ->
        entities.map { it.toModel() }
    }

    fun getRecentlyInstalledApps(timeLimit: Long): Flow<List<AppModel>> =
        appDao.getRecentlyInstalledApps(timeLimit).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun incrementUsage(packageName: String) {
        appDao.incrementUsage(packageName, System.currentTimeMillis())
    }

    suspend fun setHidden(packageName: String, isHidden: Boolean) {
        appDao.setHidden(packageName, isHidden)
    }

    fun launchApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openInPlayStore(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = "market://details?id=$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun openAppInfo(packageName: String) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:$packageName".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun AppEntity.toModel(): AppModel {
        val icon = try {
            packageManager.getApplicationIcon(packageName)
        } catch (_: Exception) {
            null
        }
        return AppModel(
            packageName = packageName,
            label = label,
            icon = icon,
            usageCount = usageCount,
            isHidden = isHidden,
            installTime = installTime,
            lastUsedTime = lastUsedTime
        )
    }

    fun isInitialRefreshDone(): Boolean = prefs.getBoolean(KEY_INITIAL_REFRESH_DONE, false)

    suspend fun refreshApps() {
        if (isInitialRefreshDone()) return

        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val entities = resolveInfos.mapNotNull { it.toEntity() }
        appDao.insertApps(entities)
        appDao.deleteRemovedApps(entities.map { it.packageName })
        
        prefs.edit { 
            putBoolean(KEY_INITIAL_REFRESH_DONE, true)
        }
    }

    suspend fun refreshApp(packageName: String) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            `setPackage`(packageName)
        }
        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val entities = resolveInfos.mapNotNull { it.toEntity() }
        if (entities.isNotEmpty()) {
            appDao.insertApps(entities)
        } else {
            // If it no longer has a launcher activity, we should probably remove it
            appDao.deleteApp(packageName)
        }
    }

    suspend fun removeApp(packageName: String) {
        appDao.deleteApp(packageName)
    }

    private fun android.content.pm.ResolveInfo.toEntity(): AppEntity? {
        val pkgName = activityInfo.packageName
        val label = loadLabel(packageManager).toString()
        val installTime = try {
            packageManager.getPackageInfo(pkgName, 0).firstInstallTime
        } catch (_: Exception) {
            0L
        }
        return AppEntity(
            packageName = pkgName,
            label = label,
            installTime = installTime
        )
    }
}
