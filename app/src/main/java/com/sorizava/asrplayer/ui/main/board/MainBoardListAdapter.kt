/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sorizava.asrplayer.data.item.BoardDetailItem
import com.sorizava.asrplayer.ui.base.ItemSelectedListener
import org.mozilla.focus.databinding.AdapterBoardItemBinding


class MainBoardListAdapter(
    private val boardItemList: ArrayList<BoardDetailItem>,
    private val listener: ItemSelectedListener<BoardDetailItem>
) : RecyclerView.Adapter<MainBoardListAdapter.BoardListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        val itemBinding =
            AdapterBoardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardListViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        holder.bind(boardItemList[position], listener)
    }

    override fun getItemCount(): Int {
        return boardItemList.size
    }

    inner class BoardListViewHolder(
        private val itemBinding: AdapterBoardItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: BoardDetailItem, listener: ItemSelectedListener<BoardDetailItem>) {
            itemBinding.apply {
                txtTitle.text = item.title
                layoutItem.setOnClickListener {
                    listener.onSelectedItem(item)
                }
            }
        }
    }
}


