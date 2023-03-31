/*
 * Create by jhong on 2022. 8. 2.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import android.app.Application
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.item.EventItem
import com.sorizava.asrplayer.data.item.FaqItem
import com.sorizava.asrplayer.data.item.NoticeItem
import com.sorizava.asrplayer.network.AppApiClient
import com.sorizava.asrplayer.network.BoardApiClient
import com.sorizava.asrplayer.utils.NetworkHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class BoardRepository(private val context: Application): BoardRepositorySource {
    override suspend fun requestNoticeItems(): Flow<ResultState<List<NoticeItem>>> {
        return flow {
            emit(BoardApiClient(NetworkHandler(context), AppApiClient.boardApiService).requestNoticeItems())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun requestEventItems(): Flow<ResultState<List<EventItem>>> {
        return flow {
            emit(BoardApiClient(NetworkHandler(context), AppApiClient.boardApiService).requestEventItems())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun requestFaqItems(): Flow<ResultState<List<FaqItem>>> {
        return flow {
            emit(BoardApiClient(NetworkHandler(context), AppApiClient.boardApiService).requestFaqItems())
        }.flowOn(Dispatchers.IO)
    }
}