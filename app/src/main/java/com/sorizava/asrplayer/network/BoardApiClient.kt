/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.network

import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.item.EventItem
import com.sorizava.asrplayer.data.item.FaqItem
import com.sorizava.asrplayer.data.item.NoticeItem
import com.sorizava.asrplayer.utils.NetworkConnectivity

class BoardApiClient(
    networkConnectivity: NetworkConnectivity,
    private val apiService: BoardApiService
) : BaseApiClient(networkConnectivity) {

    suspend fun requestNoticeItems(): ResultState<List<NoticeItem>> {
        return when (
            val response = processCall { apiService.requestNoticeItems()}
        ) {
            is Int -> {
                ResultState.Error(errorCode = response)
            }
            else -> {
                val list = ArrayList<NoticeItem>()
                list.addAll(response as List<NoticeItem>)
                ResultState.Success(data = list)
            }
        }
    }

    suspend fun requestFaqItems(): ResultState<List<FaqItem>> {
        return when (
            val response = processCall { apiService.requestFaqItems()}
        ) {
            is Int -> {
                ResultState.Error(errorCode = response)
            }
            else -> {
                val result = response as List<FaqItem>
                ResultState.Success(data = result)
            }
        }
    }

    suspend fun requestEventItems(): ResultState<List<EventItem>> {
        return when (
            val response = processCall { apiService.requestEventItems()}
        ) {
            is Int -> {
                ResultState.Error(errorCode = response)
            }
            else -> {
                val result = response as List<EventItem>
                ResultState.Success(data = result)
            }
        }
    }
}