/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.sorizava.asrplayer.ui.settings

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.preference.ListPreference
import androidx.preference.PreferenceViewHolder
import org.mozilla.focus.R

/**
 * 테마 변경 preference
 */
class ThemePreference(context: Context?, attrs: AttributeSet?) : ListPreference(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder?.findViewById(android.R.id.title)
        showIcon(holder)
    }

    private fun showIcon(holder: PreferenceViewHolder?) {
        val widgetFrame: View? = holder?.findViewById(android.R.id.widget_frame)
        widgetFrame?.isVisible = true
    }
}
