/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.config

import android.content.Context
import android.content.SharedPreferences

private const val KEY_USER_SNS_ID = "userSNSId"
private const val KEY_USER_SNS_TYPE = "userSNSType"
private const val KEY_DEVICE_TOKEN = "deviceToken"

/** 2021.10.31 생년월일, 폰번호 저장 pref  */
private const val PREF_KEY_USER_BIRTH = "PREF_KEY_USER_BIRTH"
private const val PREF_KEY_USER_PHONE = "PREF_KEY_USER_PHONE"

/** 2021.11.01 웹서버 ID pref  */
private const val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"

class EarzoomLoginManager(applicationContext: Context) {
    private val appPrefs: SharedPreferences
    fun clear() {
        val edit = appPrefs.edit()
        edit.remove(KEY_USER_SNS_ID)
        edit.remove(KEY_USER_SNS_TYPE)
        edit.remove(PREF_KEY_USER_BIRTH)
        edit.remove(PREF_KEY_USER_PHONE)
        edit.remove(PREF_KEY_USER_ID)
        edit.apply()
    }

    fun putUserId(id: String?) {
        val edit = appPrefs.edit()
        edit.putString(KEY_USER_SNS_ID, id)
        edit.apply()
    }

    val userId: String?
        get() = appPrefs.getString(KEY_USER_SNS_ID, "")

    fun putUserSNSType(type: Int) {
        val edit = appPrefs.edit()
        edit.putInt(KEY_USER_SNS_TYPE, type)
        edit.apply()
    }

    val userSNSType: Int
        get() = appPrefs.getInt(KEY_USER_SNS_TYPE, SNS_TYPE_NONE)

    fun putDeviceToken(token: String?) {
        val edit = appPrefs.edit()
        edit.putString(KEY_DEVICE_TOKEN, token)
        edit.apply()
    }

    val deviceToken: String?
        get() = appPrefs.getString(KEY_DEVICE_TOKEN, "device_token_test")

    /** 2021.10.31 생년월일 pref  */
    var prefUserBirth: String?
        get() = appPrefs.getString(PREF_KEY_USER_BIRTH, "")
        set(date) {
            val edit = appPrefs.edit()
            edit.putString(PREF_KEY_USER_BIRTH, date)
            edit.apply()
        }

    /** 2021.10.31 폰번호 pref  */
    var prefUserPhone: String?
        get() = appPrefs.getString(PREF_KEY_USER_PHONE, "")
        set(phone) {
            val edit = appPrefs.edit()
            edit.putString(PREF_KEY_USER_PHONE, phone)
            edit.apply()
        }

    /** 2021.10.31 User id pref  */
    var prefUserId: String?
        get() = appPrefs.getString(PREF_KEY_USER_ID, "")
        set(id) {
            val edit = appPrefs.edit()
            edit.putString(PREF_KEY_USER_ID, id)
            edit.apply()
        }

    companion object {
        const val SNS_TYPE_NONE = -1
        const val SNS_TYPE_NAVER = 0
        const val SNS_TYPE_KAKAO = 1
        const val SNS_TYPE_FACEBOOK = 2
        const val SNS_TYPE_GOOGLE = 3
        @JvmStatic
        var instance: EarzoomLoginManager? = null
            private set

        fun onInit(applicationContext: Context) {
            if (instance == null) {
                instance = EarzoomLoginManager(applicationContext)
            }
        }
    }

    init {
        appPrefs = applicationContext.getSharedPreferences("shared", 0)
    }
}