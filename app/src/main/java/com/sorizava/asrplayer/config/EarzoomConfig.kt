/*
 * Create by jhong on 2022. 1. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.config

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import com.sorizava.asrplayer.extension.getSharedPrefs
import org.mozilla.focus.R

/** ## 설정 클래스 */
open class EarzoomConfig(val context: Context) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: EarzoomConfig? = null

        fun getInstance(context: Context): EarzoomConfig {
            return instance ?: synchronized(this) {
                instance ?: EarzoomConfig(context).also {
                    instance = it
                }
            }
        }
    }

    private val prefs = context.getSharedPrefs()

    var appId: String
        get() = prefs.getString(APP_ID, "")!!
        set(appId) = prefs.edit().putString(APP_ID, appId).apply()

    var naverClientId: String
        get() = prefs.getString(NAVER_CLIENT_ID, context.resources.getString(R.string.naver_client_id))!!
        set(naverClientId) = prefs.edit().putString(NAVER_CLIENT_ID, naverClientId).apply()

    var naverClientSecret: String
        get() = prefs.getString(NAVER_CLIENT_SECRET, context.resources.getString(R.string.naver_client_secret))!!
        set(naverClientSecret) = prefs.edit().putString(NAVER_CLIENT_SECRET, naverClientSecret).apply()

    var naverClientName: String
        get() = prefs.getString(NAVER_CLIENT_NAME,  context.resources.getString(R.string.naver_client_name))!!
        set(naverClientName) = prefs.edit().putString(NAVER_CLIENT_NAME, naverClientName).apply()
}
