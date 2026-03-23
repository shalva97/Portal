package com.portal.browserbar.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.edit
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
        private const val REFRESH_COOLDOWN = 30 * 60 * 1000L // 30 minutes
        private const val KEY_LAST_REFRESH = "last_refresh_time"
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
            data = Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openInPlayStore(packageName: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            context.startActivity(intent)
        }
    }

    private fun AppEntity.toModel(): AppModel {
        val icon = try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
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

    suspend fun refreshApps(force: Boolean = false) {
        val currentTime = System.currentTimeMillis()
        val lastRefresh = prefs.getLong(KEY_LAST_REFRESH, 0L)

        if (!force && (currentTime - lastRefresh < REFRESH_COOLDOWN)) {
            return
        }

        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val entities = resolveInfos.map {
            val packageName = it.activityInfo.packageName
            val label = it.loadLabel(packageManager).toString()
            val installTime = try {
                packageManager.getPackageInfo(packageName, 0).firstInstallTime
            } catch (e: Exception) {
                0L
            }
            AppEntity(
                packageName = packageName,
                label = label,
                installTime = installTime
            )
        }
        appDao.insertApps(entities)
        appDao.deleteRemovedApps(entities.map { it.packageName })
        
        prefs.edit { putLong(KEY_LAST_REFRESH, currentTime) }
    }
}
