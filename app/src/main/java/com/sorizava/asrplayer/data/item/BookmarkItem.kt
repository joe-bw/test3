/*
 * Create by jhong on 2022. 7. 28.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.data.item

import androidx.annotation.DrawableRes

data class BookmarkItem(

    @DrawableRes var img: Int,
    var bookmarkId: Int,
    var position: Int,
    var imgName: String,
    var imgUrl: String? = null,
    var name: String,
    var url: String,
    val isAdd: Boolean = false,
)
