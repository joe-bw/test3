/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import android.app.Application
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.SnsProvider
import com.sorizava.asrplayer.data.vo.LoginDataVO
import com.sorizava.asrplayer.data.vo.LoginNewRequest
import com.sorizava.asrplayer.network.AppApiClient
import com.sorizava.asrplayer.network.AppApiResponse
import com.sorizava.asrplayer.network.LoginApiClient
import com.sorizava.asrplayer.utils.NetworkHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginRepository(
    private val context: Application,
    private val info: LoginNewRequest
) : LoginRepositorySource {
    override suspend fun requestMemberInfo(): Flow<ResultState<AppApiResponse<LoginDataVO>>> {
        return flow {
            val networkConnectivity = NetworkHandler(context)
            val apiService = AppApiClient.apiService
            emit(LoginApiClient(networkConnectivity, apiService).requestMemberInfo(info))
        }.flowOn(Dispatchers.IO)
    }
}