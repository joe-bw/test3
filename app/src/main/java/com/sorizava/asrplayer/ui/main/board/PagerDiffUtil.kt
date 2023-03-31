/*
 * Create by jhong on 2022. 10. 20.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import androidx.recyclerview.widget.DiffUtil
import com.sorizava.asrplayer.data.item.BoardItem

class PagerDiffUtil(private val oldList: List<BoardItem>, private val newList: List<BoardItem>) : DiffUtil.Callback() {

    enum class PayloadKey {
        VALUE
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].idx == newList[newItemPosition].idx
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].idx == newList[newItemPosition].idx
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return listOf(PayloadKey.VALUE)
    }
}