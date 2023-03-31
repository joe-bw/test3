/*
 * Create by jhong on 2022. 10. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.sorizava.asrplayer.extension.appConfig
import com.sorizava.asrplayer.extension.beVisibleIf
import kotlinx.android.synthetic.main.fragment_tutorial_item.*
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.fragment.UrlInputFragment

private const val ARG_PAGE_IDX = "page_idx"

/**
 * 도움말 화면
 * 총 4개의 페이지로 이루어진 화면
 * paging, indication UI 적용
 */
class TutorialItemFragment : Fragment(), View.OnClickListener {
    @DrawableRes
    private var paramPageIdx: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            paramPageIdx = it.getInt(ARG_PAGE_IDX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tutorial_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_not_again.isChecked = activity?.appConfig?.prefTutorialCheck == true
        btn_not_again.setOnCheckedChangeListener { _, isChecked ->
            activity?.appConfig?.prefTutorialCheck = isChecked
        }

        btn_close.setOnClickListener(this)

        paramPageIdx?.let {
            img_tutorial.setBackgroundResource(it)
            btn_close.beVisibleIf(paramPageIdx == R.drawable.bg_tuto4)
            btn_not_again.beVisibleIf(paramPageIdx == R.drawable.bg_tuto4)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_close -> {
                requireActivity().finish()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) = TutorialItemFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PAGE_IDX, param1)
            }
        }
    }
}