/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.data.vo

import com.google.gson.annotations.SerializedName

/**
 * {
 * "statisticsSeq": "11",
 * "endTime":"20210803_154500"
 * }
 */
class EndStatisticsRequest(
    @field:SerializedName("statisticsSeq")
    var statisticsSeq: String,

    @field:SerializedName("endTime")
    var endTime: String
)