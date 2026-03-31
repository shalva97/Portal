package com.portal.browserbar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps WHERE isHidden = 0 ORDER BY usageCount DESC, label ASC")
    fun getVisibleApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps ORDER BY label ASC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE isHidden = 0 ORDER BY lastUsedTime DESC LIMIT 8")
    fun getRecentlyUsedApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE installTime > :timeLimit ORDER BY installTime DESC")
    fun getRecentlyInstalledApps(timeLimit: Long): Flow<List<AppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApps(apps: List<AppEntity>)

    @Update
    suspend fun updateApp(app: AppEntity)

    @Query("UPDATE apps SET usageCount = usageCount + 1, lastUsedTime = :currentTime WHERE packageName = :packageName")
    suspend fun incrementUsage(packageName: String, currentTime: Long)

    @Query("UPDATE apps SET isHidden = :isHidden WHERE packageName = :packageName")
    suspend fun setHidden(packageName: String, isHidden: Boolean)

    @Query("DELETE FROM apps WHERE packageName NOT IN (:packageNames)")
    suspend fun deleteRemovedApps(packageNames: List<String>)

    @Query("DELETE FROM apps WHERE packageName = :packageName")
    suspend fun deleteApp(packageName: String)
}
