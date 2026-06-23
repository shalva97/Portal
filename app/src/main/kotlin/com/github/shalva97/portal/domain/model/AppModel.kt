package com.github.shalva97.portal.domain.model

data class AppModel(
    val packageName: String,
    val label: String,
    val iconPath: String? = null,
    val usageCount: Int = 0,
    val isHidden: Boolean = false,
    val installTime: Long = 0L,
    val lastUsedTime: Long = 0L,
    val isGame: Boolean = false
)
