/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.data.vo

import com.google.gson.annotations.SerializedName

/**
 * {
 * "id":"sorizava",
 * "connDate":"2021-08-03",
 * "stTime":"20210803_154400",
 * "useContent":"http://www.useContent.com"
 * }
 */
class StartStatisticsRequest(
    @field:SerializedName("id")
    var id: String,

    @field:SerializedName("connDate")
    var connDate: String,

    @field:SerializedName("stTime")
    var stTime: String,

    @field:SerializedName("useContent")
    var useContent: String
)