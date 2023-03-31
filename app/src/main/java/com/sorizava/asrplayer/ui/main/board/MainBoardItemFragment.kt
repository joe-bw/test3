/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sorizava.asrplayer.config.EARZOOM_BOARD_BASE_URL
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.item.BoardDetailItem
import com.sorizava.asrplayer.extension.*
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.base.ItemSelectedListener
import org.mozilla.focus.databinding.FragmentMainBoardBinding
import org.mozilla.focus.locale.LocaleAwareAppCompatActivity

private const val ARG_BOARD_IDX = "board_idx"

class MainBoardItemFragment : BaseFragment<FragmentMainBoardBinding>(
    FragmentMainBoardBinding::inflate
), ItemSelectedListener<BoardDetailItem> {

    private lateinit var viewModel: MainBoardViewModel

    private val boardIdx: Int by argumentParams(ARG_BOARD_IDX, 0)

    private var boardListAdapter: MainBoardListAdapter? = null

    companion object {
        fun newInstance(mediaIdx: Int) =
            MainBoardItemFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(ARG_BOARD_IDX, mediaIdx)
                }
            }
    }

    override fun initView() {

        val context = activity?.applicationContext

        viewModel = ViewModelProvider(this, BoardItemViewModelFactory(context as Application))
            .get(MainBoardViewModel::class.java)

        requestBoardList(boardIdx)
    }

    private fun requestBoardList(boardIdx: Int) {
        viewModel.requestBoardList(boardIdx)
    }

    override fun initViewModelObserver() {
        observe(viewModel.boardDetailItems, ::handleList)
    }

    private fun handleList(resultState: ResultState<ArrayList<BoardDetailItem>>) {

        when (resultState) {
            is ResultState.Loading -> showLoadingView()
            is ResultState.Success -> {
                hideLoadingView()
                if (resultState.data?.size!! > 0) {
                    bindListData(list = resultState.data)
                } else {
                    showDataView(false)
                }
            }
            else -> {
                showDataView(false)
                hideLoadingView()
            }
        }
    }

    private fun showDataView(exist: Boolean) {
        binding.apply {
            listBoard.beVisibleIf(exist)
            itemsEmpty.beGoneIf(exist)
        }
    }

    private fun bindListData(list: ArrayList<BoardDetailItem>) {
        boardListAdapter = MainBoardListAdapter(list, this)
        binding.listBoard.adapter = boardListAdapter

        showDataView(true)
    }

    private fun hideLoadingView() = binding.pbLoading.beGone()
    private fun showLoadingView() = binding.pbLoading.beVisible()

    override fun onSelectedItem(item: BoardDetailItem) {
        var url = item.url
        if (url.startsWith("/")) {
            url = url.substring(1, url.length)
        }
        (activity as LocaleAwareAppCompatActivity).openNotice(EARZOOM_BOARD_BASE_URL + url)
    }
}

class BoardItemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainBoardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            MainBoardViewModel(application) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}