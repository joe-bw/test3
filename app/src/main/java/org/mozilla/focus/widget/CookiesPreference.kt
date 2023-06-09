/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.widget

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import androidx.preference.ListPreference
import androidx.preference.PreferenceViewHolder
import kotlinx.android.synthetic.main.fx_toast.view.*
import org.mozilla.focus.utils.Settings

/**
 * Autocomplete preference that will show a sub screen to configure the autocomplete behavior.
 */
class CookiesPreference(context: Context?, attrs: AttributeSet?) : ListPreference(context, attrs) {

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder?.findViewById(R.id.title)
        updateSummary()
        showIcon(holder)
    }

    override fun notifyChanged() {
        super.notifyChanged()
        updateSummary()
    }

    fun updateSummary() {
        val settings = Settings.getInstance(context)
        super.setSummary(settings.shouldBlockCookiesValue())
    }

    private fun showIcon(holder: PreferenceViewHolder?) {
        val widgetFrame: View? = holder?.findViewById(R.id.widget_frame)
        widgetFrame?.isVisible = true
    }
}
