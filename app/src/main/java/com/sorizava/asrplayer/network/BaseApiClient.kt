/*
 * Create by jhong on 2022. 6. 14.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.network

import com.sorizava.asrplayer.data.ErrorCode
import com.sorizava.asrplayer.utils.NetworkConnectivity
import retrofit2.Response
import java.io.IOException

open class BaseApiClient (
    private val networkConnectivity: NetworkConnectivity,
) {
    suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {

        if (!networkConnectivity.isNetworkAvailable()) {
            return ErrorCode.NO_INTERNET_CONNECTION.ordinal
        }

        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            if (response.isSuccessful) {
                response.body()
            } else {
                responseCode
            }
        } catch (e: IOException) {
            ErrorCode.NETWORK_ERROR.ordinal
        }
    }
}