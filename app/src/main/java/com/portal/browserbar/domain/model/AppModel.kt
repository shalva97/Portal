package com.portal.browserbar.domain.model

import android.graphics.drawable.Drawable

data class AppModel(
    val packageName: String,
    val label: String,
    val icon: Drawable? = null,
    val usageCount: Int = 0,
    val isHidden: Boolean = false,
    val installTime: Long = 0L,
    val lastUsedTime: Long = 0L
)
