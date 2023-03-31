/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.tutorial

import com.sorizava.asrplayer.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_tutorial.*
import org.mozilla.focus.R
import org.mozilla.focus.databinding.FragmentTutorialBinding

/**
 * 튜토리얼 화면
 */
class TutorialFragment : BaseFragment<FragmentTutorialBinding>(FragmentTutorialBinding::inflate) {

    companion object {
        fun newInstance() = TutorialFragment()
    }

    override fun initView() {

        val list = listOf(
            R.drawable.bg_tuto1,
            R.drawable.bg_tuto2,
            R.drawable.bg_tuto3,
            R.drawable.bg_tuto4,
        )

        val adapter = TutorialPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        adapter.setItems(list)

        pager.adapter = adapter
        dots_indicator2.setViewPager2(pager)
    }

    override fun setBackPressed() {
        super.setBackPressed()
        requireActivity().finish()
    }
}