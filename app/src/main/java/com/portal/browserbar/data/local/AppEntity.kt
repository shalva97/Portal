package com.portal.browserbar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps")
data class AppEntity(
    @PrimaryKey val packageName: String,
    val label: String,
    val usageCount: Int = 0,
    val isHidden: Boolean = false,
    val installTime: Long = 0L,
    val lastUsedTime: Long = 0L,
    val iconPath: String? = null
)
