/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

/**
 * Event 리스트
 */
data class EventItem(
    var eventSeq: Int? = null,
    var eventTitle: String? = null,
    var eventContent: String? = null,
    var eventWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
