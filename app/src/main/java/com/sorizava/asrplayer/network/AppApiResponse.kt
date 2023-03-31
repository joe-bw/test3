/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Response
 */
class AppApiResponse<T> {
    @JvmField
    @SerializedName("status")
    var status = 0

    @JvmField
    @Expose
    @SerializedName("data")
    var data: T? = null

    @SerializedName("code")
    var code: String? = null
    override fun toString(): String {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", code='" + code + '\'' +
                '}'
    }
}