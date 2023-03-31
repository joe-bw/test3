/*
 * Create by jhong on 2022. 2. 16.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import android.app.Activity
import org.mozilla.focus.R

fun Activity.switchStartScreenAnimation() {
    overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_none)
}