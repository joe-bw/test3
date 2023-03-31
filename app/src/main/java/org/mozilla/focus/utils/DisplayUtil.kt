package org.mozilla.focus.utils

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics

/**
 * @Author: duke
 * @DateTime: 2021-03-17 12:26:47
 * @Description: DisplayUtil：
 */
object DisplayUtil {

    private fun getDisplayMetrics(context: Context?): DisplayMetrics? {
        context ?: return null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val dm = DisplayMetrics()
            context.display?.getRealMetrics(dm)
            dm
        } else {
            context.resources.displayMetrics
        }
    }

    fun getWidthPixels(context: Context?): Int {
        val dm = getDisplayMetrics(context) ?: return 0
        return dm.widthPixels
    }

    fun getHeightPixels(context: Context?): Int {
        val dm = getDisplayMetrics(context) ?: return 0
        return dm.heightPixels
    }

}