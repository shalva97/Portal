package com.portal.browserbar.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

class IconStorage(private val context: Context) {
    private val iconDir = File(context.filesDir, "app_icons").apply {
        if (!exists()) mkdirs()
    }

    fun saveIcon(packageName: String, drawable: Drawable): String? {
        val file = File(iconDir, "$packageName.png")
        return try {
            val bitmap = drawableToBitmap(drawable)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun getIconPath(packageName: String): String? {
        val file = File(iconDir, "$packageName.png")
        return if (file.exists()) file.absolutePath else null
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap = createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1)
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
