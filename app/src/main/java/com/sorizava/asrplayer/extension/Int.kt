/*
 * Create by jhong on 2022. 2. 14.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import android.content.res.Resources
import android.text.format.DateFormat
import java.util.Locale
import java.util.Calendar

fun Int.dp(): Float {
    return this * Resources.getSystem().displayMetrics.density
}

fun Int.textSizeByDensity(): Float {
    val density = Resources.getSystem().displayMetrics.density
    return this * (2.5f / density)
}
