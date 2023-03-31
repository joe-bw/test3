/*
 * Create by jhong on 2022. 7. 12.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data

/** 인트로 상태 Enum 클래스 */
enum class IntroState {
    LOADING,
    NEED_APP_UPDATE,
    CHECK_LOGIN,
    GOTO_LOGIN,
    GOTO_MAIN,
    GOTO_SIGN_UP,
    FAILED_LOGIN
}