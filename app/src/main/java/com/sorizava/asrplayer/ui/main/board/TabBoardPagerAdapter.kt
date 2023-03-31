/*
 * Create by jhong on 2022. 10. 20.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sorizava.asrplayer.data.item.BoardItem
import java.lang.ref.WeakReference

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class TabBoardPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentCache = mutableMapOf<Int, WeakReference<MainBoardItemFragment>>()

    private val dataList: ArrayList<BoardItem> = arrayListOf()

    override fun getItemCount() = dataList.size

    override fun createFragment(position: Int): MainBoardItemFragment {

        fragmentCache[position]?.get()?.let { return it }

        return MainBoardItemFragment.newInstance(dataList[position].idx)
            .also { fragmentCache[position] = WeakReference(it) }
    }

    fun setItems(newItems: List<BoardItem>) {
        val callback = PagerDiffUtil(dataList, newItems)
        val diff = DiffUtil.calculateDiff(callback)

        dataList.clear()
        dataList.addAll(newItems)

        diff.dispatchUpdatesTo(this)

        notifyItemInserted(dataList.size - 1)
    }
}
