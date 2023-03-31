/*
 * Create by jhong on 2022. 7. 26.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

/**
 * FAQ 리스트
 */
data class FaqItem(
    var faqSeq: Int? = null,
    var faqTitle: String? = null,
    var faqContent: String? = null,
    var faqWriter: String? = null,
    var regDt: String? = null,
    var modDt: String? = null,
    var linkUrl: String? = null,
)
