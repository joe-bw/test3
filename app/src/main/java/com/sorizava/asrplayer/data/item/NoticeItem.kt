/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

/**
 * 공지사항 리스트
 */
data class NoticeItem(
    var noticeSeq: Int? = null,
    var noticeTitle: String? = null,
    var noticeContent: String? = null,
    var noticeWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
