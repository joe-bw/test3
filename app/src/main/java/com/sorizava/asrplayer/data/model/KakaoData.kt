/*
 * Create by jhong on 2022. 7. 19.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.model

import com.sorizava.asrplayer.data.SnsProvider

class KakaoData(
    val token: String? = null,

    val id: String? = null,
    val account: String? = null,
    val email: String? = null,

    val errorMessage: String? = null,

    override var type: SnsProvider? = SnsProvider.KAKAO
) : SnsResultData
