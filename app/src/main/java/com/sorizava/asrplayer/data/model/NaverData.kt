/*
 * Create by jhong on 2022. 7. 19.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.model

import com.sorizava.asrplayer.data.SnsProvider

data class NaverData(

    val token: String? = null,

    val accessToken: String? = null,
    val refreshToken: String? = null,
    val expireAt: String? = null,
    val tokenType: String? = null,
    val state: String? = null,

    val errorCode: String? = null,
    val errorDescription: String? = null,

    override var type: SnsProvider? = SnsProvider.NAVER
) : SnsResultData
