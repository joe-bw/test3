/*
 * Create by jhong on 2022. 7. 19.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.model

import com.sorizava.asrplayer.data.SnsProvider

/** SNS 로그인의 return value 값들을 정리하기 위한 interface 정의 */
interface SnsResultData {
    var type: SnsProvider?
}