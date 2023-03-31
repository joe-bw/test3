/*
 * Create by jhong on 2022. 6. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainUIAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val dataList: ArrayList<Fragment> = arrayListOf()

    fun setItems(items: ArrayList<Fragment>) {
        dataList.clear()
        dataList.addAll(items)
        notifyItemInserted(dataList.size - 1)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun createFragment(position: Int): Fragment {
        return dataList[position]
    }
}