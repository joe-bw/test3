/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.bookmark

interface BookmarkItemSelectedListener<T> {
    fun onSelectedItem(item: T, mode: Boolean, position: Int)
    fun onLongSelectedItem(item: T, position: Int)
    fun onAppliedItems()
}