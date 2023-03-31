/*
 * Create by jhong on 2022. 1. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.sorizava.asrplayer.config.EarzoomConfig
import com.sorizava.asrplayer.config.PREFS_KEY
import com.sorizava.asrplayer.config.isOnMainThread
import kr.co.sorizava.asrplayer.AppConfig


// Config 를 쉽게 전역으로 사용할 수 있도록 각 프로젝트 마다 설정 필요
val Context.config: EarzoomConfig get() = EarzoomConfig.getInstance(applicationContext)
val Context.appConfig: AppConfig get() = AppConfig.getInstance(applicationContext)

fun Context.getSharedPrefs(): SharedPreferences =
    getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)


fun Context.getVersion(): String =
    this.packageManager.getPackageInfo(this.packageName, 0).versionName

fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT): Toast? = toast(getString(id), length)

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT): Toast? {
    try {
        if (isOnMainThread()) {
            return doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun doToast(context: Context, message: String, length: Int): Toast? {
    val toast = Toast.makeText(context, message, length)
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            toast.show()
            return toast
        }
    } else {
        toast.show()
        return toast
    }
    return null
}

val Context.connectivityManager: ConnectivityManager
    get() =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
