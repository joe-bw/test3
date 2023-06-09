/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.settings.privacy

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import org.mozilla.focus.R
import org.mozilla.focus.biometrics.Biometrics
import org.mozilla.focus.engine.EngineSharedPreferencesListener
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.settings.BaseSettingsFragment
import org.mozilla.focus.state.AppAction
import org.mozilla.focus.state.Screen
import org.mozilla.focus.telemetry.TelemetryWrapper
import org.mozilla.focus.utils.Settings
import org.mozilla.focus.widget.CookiesPreference

class PrivacySecuritySettingsFragment :
    BaseSettingsFragment(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.privacy_security_settings)
        val cookiesPreference =
            findPreference(getString(R.string.pref_key_performance_enable_cookies)) as? CookiesPreference

        cookiesPreference?.updateSummary()
    }

    override fun onResume() {
        super.onResume()
        updateExceptionSettingAvailability()

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        // Update title and icons when returning to fragments.
        updateTitle(R.string.preference_privacy_and_security_header)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        TelemetryWrapper.settingsEvent(key, sharedPreferences.all[key].toString())
    }

    private fun updateExceptionSettingAvailability() {
        val exceptionsPreference: Preference? = findPreference(getString(R.string.pref_key_screen_exceptions))
        exceptionsPreference?.isEnabled = false


        requireComponents.trackingProtectionUseCases.fetchExceptions.invoke { exceptions ->
            exceptionsPreference?.isEnabled = exceptions.isNotEmpty()
        }
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        val settings = Settings.getInstance(requireContext())
        val engineSharedPreferencesListener = EngineSharedPreferencesListener(requireContext())
        when (preference.key) {
            resources.getString(R.string.pref_key_screen_exceptions) -> {
                TelemetryWrapper.openExceptionsListSetting()
                requireComponents.appStore.dispatch(
                    AppAction.OpenSettings(page = Screen.Settings.Page.PrivacyExceptions)
                )
            }
            resources.getString(R.string.pref_key_secure),
            resources.getString(R.string.pref_key_biometric) -> {
                // We need to recreate the activity to apply the SECURE flags.
                requireActivity().recreate()
            }

            resources.getString(R.string.pref_key_privacy_block_social) ->
                engineSharedPreferencesListener.updateTrackingProtectionPolicy(
                    EngineSharedPreferencesListener.ChangeSource.SETTINGS.source,
                    EngineSharedPreferencesListener.TrackerChanged.SOCIAL.tracker,
                    settings.shouldBlockSocialTrackers()
                )

            resources.getString(R.string.pref_key_privacy_block_ads) ->
                engineSharedPreferencesListener.updateTrackingProtectionPolicy(
                    EngineSharedPreferencesListener.ChangeSource.SETTINGS.source,
                    EngineSharedPreferencesListener.TrackerChanged.ADVERTISING.tracker,
                    settings.shouldBlockAdTrackers()
                )

            resources.getString(R.string.pref_key_privacy_block_analytics) ->
                engineSharedPreferencesListener.updateTrackingProtectionPolicy(
                    EngineSharedPreferencesListener.ChangeSource.SETTINGS.source,
                    EngineSharedPreferencesListener.TrackerChanged.ANALYTICS.tracker,
                    settings.shouldBlockAnalyticTrackers()
                )

            resources.getString(R.string.pref_key_privacy_block_other3) ->
                engineSharedPreferencesListener.updateTrackingProtectionPolicy(
                    EngineSharedPreferencesListener.ChangeSource.SETTINGS.source,
                    EngineSharedPreferencesListener.TrackerChanged.CONTENT.tracker,
                    settings.shouldBlockOtherTrackers()
                )
        }
        return super.onPreferenceTreeClick(preference)
    }

    companion object {
        const val FRAGMENT_TAG = "PrivacySecuritySettings"

        fun newInstance(): PrivacySecuritySettingsFragment {
            return PrivacySecuritySettingsFragment()
        }
    }
}
