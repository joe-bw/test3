package com.sorizava.asrplayer.application

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.mozilla.focus.R
import com.sorizava.asrplayer.application.FBRemoteConfigManager

class FBRemoteConfigManager {

    private var remoteConfig: FirebaseRemoteConfig? = null

    fun init() {
        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig!!.setConfigSettingsAsync(configSettings)
        remoteConfig!!.fetch().addOnCompleteListener {
            remoteConfig!!.activate().addOnCompleteListener {
                Log.e(
                    FBRemoteConfigManager::class.java.simpleName,
                    "$AOS_APP_VERSION_NAME : " + remoteConfig!!.getString(AOS_APP_VERSION_NAME)
                )
            }
        }
    }

    fun getString(key: String): String {
        return remoteConfig!!.getString(key)
    }

    companion object {
        const val AOS_APP_VERSION_NAME = "aosAppVersionName"
    }
}