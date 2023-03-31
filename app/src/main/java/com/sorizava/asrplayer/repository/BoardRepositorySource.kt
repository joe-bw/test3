/*
 * Create by jhong on 2022. 7. 13.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.repository

import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.item.EventItem
import com.sorizava.asrplayer.data.item.FaqItem
import com.sorizava.asrplayer.data.item.NoticeItem
import kotlinx.coroutines.flow.Flow

interface BoardRepositorySource {
    suspend fun requestNoticeItems(): Flow<ResultState<List<NoticeItem>>>
    suspend fun requestEventItems(): Flow<ResultState<List<EventItem>>>
    suspend fun requestFaqItems(): Flow<ResultState<List<FaqItem>>>
}