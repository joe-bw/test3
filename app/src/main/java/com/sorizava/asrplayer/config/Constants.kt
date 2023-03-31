/*
 * Create by jhong on 2022. 1. 10.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.config

import android.os.Looper
import kr.co.sorizava.asrplayer.ZerothDefine

/** # 앱에서 사용하는 모든 상수 값 모음 */

const val TAG = "EARZOOM"

const val PREFS_KEY = "PrefsEarzoom"

const val APP_ID = "app_id"

/** 로그인 화면 진입시 UI 상태를 나타내는 flag 값을 의미
 * relogin type은 로그인을 다시 해야하는 사항이 발생하여 로그인 화면을
 *
 * */
const val LOGIN_TYPE_RELOGIN = "relogin"

const val NAVER_CLIENT_ID = "naver_client_id"
const val NAVER_CLIENT_SECRET = "naver_client_secret"
const val NAVER_CLIENT_NAME = "naver_client_name"

const val EARZOOM_BOARD_BASE_URL = ZerothDefine.BASE_URL

const val API_NOTICE_URL = "api/board/noticeView"
const val API_FAQ_URL = "api/board/faqView"
const val API_EVENT_URL = "api/board/eventView"

fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
