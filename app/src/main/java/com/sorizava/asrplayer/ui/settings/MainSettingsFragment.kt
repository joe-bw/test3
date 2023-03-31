/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.sorizava.asrplayer.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.MATCH_PARENT
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.sorizava.asrplayer.ui.privacy.PrivacyPolicyActivity
import kr.co.sorizava.asrplayer.SubtitleSettingActivity
import org.mozilla.focus.R
import org.mozilla.focus.activity.BookmarksActivity
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.locale.LocaleManager
import org.mozilla.focus.locale.Locales
import org.mozilla.focus.settings.BaseSettingsFragment
import org.mozilla.focus.settings.InstalledSearchEnginesSettingsFragment
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.widget.DefaultBrowserPreference
import org.mozilla.focus.widget.LocaleListPreference
import java.util.*

class MainSettingsFragment :
    BaseSettingsFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private var localeUpdated: Boolean = false

    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.settings_main)
    }

    override fun onResume() {
        super.onResume()

        val preference =
            findPreference(getString(R.string.pref_key_default_browser)) as? DefaultBrowserPreference
        preference?.update()

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        updateTitle(R.string.menu_settings)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {

        val resources = resources

        val page = when (preference.key) {

            resources.getString(R.string.pref_key_asr_main_caption_screen) -> {
                val intent = Intent().run {
                    setClass(requireContext(), SubtitleSettingActivity::class.java)
                }
                startActivity (intent)
                return super.onPreferenceTreeClick(preference)
            }

            resources.getString(R.string.pref_key_asr_main_bookmark_screen) -> {
                val intent = Intent().run {
                    setClass(requireContext(), BookmarksActivity::class.java)
                }
                startActivity (intent)
                return super.onPreferenceTreeClick(preference)
            }

            resources.getString(R.string.pref_key_asr_main_privacy_screen) -> {
                val intent = Intent().run {
                    setClass(requireContext(), PrivacyPolicyActivity::class.java)
                }
                startActivity (intent)
                return super.onPreferenceTreeClick(preference)
            }

            resources.getString(R.string.pref_key_general_screen) -> Screen.Settings.Page.General
            resources.getString(R.string.pref_key_search_screen) -> Screen.Settings.Page.Search
            resources.getString(R.string.pref_key_advanced_screen) -> Screen.Settings.Page.Advanced
            resources.getString(R.string.pref_key_asr_main_helper_screen) -> Screen.Settings.Page.Helper
            resources.getString(R.string.pref_key_asr_main_system_screen) -> Screen.Settings.Page.Start

            else -> throw IllegalStateException("Unknown preference: ${preference.key}")
        }

        requireComponents.appStore.dispatch(
            AppAction.OpenSettings(page)
        )

        return super.onPreferenceTreeClick(preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        TelemetryWrapper.settingsEvent(key, sharedPreferences.all[key].toString())

        if (!localeUpdated && key == getString(R.string.pref_key_locale)) {
            // Updating the locale leads to onSharedPreferenceChanged being triggered again in some
            // cases. To avoid an infinite loop we won't update the preference a second time. This
            // fragment gets replaced at the end of this method anyways.
            localeUpdated = true

            // Set langChanged from InstalledSearchEngines to true
            InstalledSearchEnginesSettingsFragment.languageChanged = true

            val languagePreference =
                findPreference(getString(R.string.pref_key_locale)) as? ListPreference
            val value = languagePreference?.value

            val localeManager = LocaleManager.getInstance()

            val locale: Locale?
            if (TextUtils.isEmpty(value)) {
                localeManager.resetToSystemLocale(activity)
                locale = localeManager.getCurrentLocale(activity)
            } else {
                locale = Locales.parseLocaleCode(value)
                localeManager.setSelectedLocale(activity, value)
            }
            localeManager.updateConfiguration(activity, locale)

            requireActivity().recreate()
        }
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        if (preference is LocaleListPreference) {
            showLoading(view as ViewGroup)
            // wait until the values are set
            preference.setEntriesListener {
                hideLoading()
                super.onDisplayPreferenceDialog(preference)
            }
        } else super.onDisplayPreferenceDialog(preference)
    }

    private var progress: FrameLayout? = null

    private fun hideLoading() {
        val root = view as ViewGroup?
        if (root != null && progress != null) {
            root.removeView(progress)
        }
    }

    private fun showLoading(root: ViewGroup) {
        progress = FrameLayout(root.context)
        val lp = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lp.gravity = Gravity.CENTER
        progress!!.addView(ProgressBar(root.context), lp)
        val lp2 = WindowManager.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        root.addView(progress, lp2)
    }

    companion object {

        fun newInstance(): MainSettingsFragment {
            return MainSettingsFragment()
        }
    }
}
