/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.network

import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.utils.NetworkConnectivity

class LoginApiClient(
    private val networkConnectivity: NetworkConnectivity,
    private val apiService: AppApiService
) : BaseApiClient(networkConnectivity) {
    suspend fun  requestMemberInfo(info: LoginNewRequest): ResultState<AppApiResponse<LoginDataVO>> {

        return when (
            val response = processCall { apiService.requestMember(info)}
        ) {
            is Int -> {
                ResultState.Error(errorCode = response)
            }
            else -> {
                val result = response as AppApiResponse<*>
                if (result.status == 200) {
                    val data: LoginDataVO = result.data as LoginDataVO
                    val member = data.member
                    EarzoomLoginManager.instance?.prefUserId = member?.id
                    ResultState.Success(data = response as AppApiResponse<LoginDataVO>)
                } else {
                    ResultState.Error(errorCode = result.status)
                }
            }
        }
    }
}