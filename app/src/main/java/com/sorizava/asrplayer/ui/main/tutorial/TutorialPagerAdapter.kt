/*
 * Create by jhong on 2022. 6. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.tutorial

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.lang.ref.WeakReference

class TutorialPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentCache = mutableMapOf<Int, WeakReference<TutorialItemFragment>>()

    private val dataList: ArrayList<Int> = arrayListOf()

    override fun getItemCount() = dataList.size

    override fun createFragment(position: Int): TutorialItemFragment {

        fragmentCache[position]?.get()?.let { return it }

        return TutorialItemFragment.newInstance(dataList[position])
            .also { fragmentCache[position] = WeakReference(it) }
    }

    fun setItems(newItems: List<Int>) {
        val callback = PagerDiffUtil(dataList, newItems)
        val diff = DiffUtil.calculateDiff(callback)

        dataList.clear()
        dataList.addAll(newItems)

        diff.dispatchUpdatesTo(this)

        notifyItemInserted(dataList.size - 1)
    }
}

class PagerDiffUtil(private val oldList: List<Int>, private val newList: List<Int>) : DiffUtil.Callback() {

    enum class PayloadKey {
        VALUE
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        return listOf(PayloadKey.VALUE)
    }
}