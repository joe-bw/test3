/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.network

import com.sorizava.asrplayer.config.API_EVENT_URL
import com.sorizava.asrplayer.config.API_FAQ_URL
import com.sorizava.asrplayer.config.API_NOTICE_URL
import com.sorizava.asrplayer.data.item.EventItem
import com.sorizava.asrplayer.data.item.FaqItem
import com.sorizava.asrplayer.data.item.NoticeItem
import retrofit2.Response
import retrofit2.http.GET

/**
 * 앱 메인에서 사용할 최신글 조회를 위한 rest api
 */
interface BoardApiService {
    @GET(API_NOTICE_URL)
    suspend fun requestNoticeItems(): Response<List<NoticeItem>>

    @GET(API_EVENT_URL)
    suspend fun requestEventItems(): Response<List<EventItem>>

    @GET(API_FAQ_URL)
    suspend fun requestFaqItems(): Response<List<FaqItem>>
}