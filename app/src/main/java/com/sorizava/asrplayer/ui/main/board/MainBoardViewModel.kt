/*
 * Create by jhong on 2022. 7. 5.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.board

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorizava.asrplayer.data.BoardType
import com.sorizava.asrplayer.data.ResultState
import com.sorizava.asrplayer.data.item.*
import com.sorizava.asrplayer.repository.BoardRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainBoardViewModel(context: Application) : ViewModel() {

    private val boardDetailItemsPrivate = MutableLiveData<ResultState<ArrayList<BoardDetailItem>>>()
    val boardDetailItems: LiveData<ResultState<ArrayList<BoardDetailItem>>> get() = boardDetailItemsPrivate

    private val boardRepository = BoardRepository(context)

    fun requestBoardList(type: Int) {

        viewModelScope.launch {

            when(type) {
                BoardType.NOTICE.type -> {
                    boardRepository.requestNoticeItems().collect {
                        boardDetailItemsPrivate.value = convertVOToNoticeItem(it)
                    }
                }

                BoardType.EVENT.type -> {
                    boardRepository.requestEventItems().collect {
                        boardDetailItemsPrivate.value = convertVOToEventItem(it)
                    }
                }
                BoardType.FAQ.type -> {
                    boardRepository.requestFaqItems().collect {
                        boardDetailItemsPrivate.value = convertVOToFaqItem(it)
                    }
                }
            }
        }
    }

    private fun convertVOToFaqItem(resultState: ResultState<List<FaqItem>>): ResultState<ArrayList<BoardDetailItem>>? {
        val list = ArrayList<BoardDetailItem>()
        return when (resultState) {
            is ResultState.Success -> {
                if (resultState.data?.size!! > 0) {
                    for (faq in resultState.data) {
                        list.add(BoardDetailItem(idx = faq.faqSeq!!, title = faq.faqTitle!!, url = faq.linkUrl!!))
                    }
                }
                ResultState.Success(list)
            }
            else -> {
                resultState.errorCode?.let { ResultState.Error(it) }
            }
        }
    }

    private fun convertVOToEventItem(resultState: ResultState<List<EventItem>>): ResultState<ArrayList<BoardDetailItem>>? {
        val list = ArrayList<BoardDetailItem>()
        return when (resultState) {
            is ResultState.Success -> {
                if (resultState.data?.size!! > 0) {
                    for (event in resultState.data) {
                        list.add(BoardDetailItem(idx = event.eventSeq!!, title = event.eventTitle!!, url = event.linkUrl!!))
                    }
                }
                ResultState.Success(list)
            }
            else -> {
                resultState.errorCode?.let { ResultState.Error(it) }
            }
        }
    }

    private fun convertVOToNoticeItem(resultState: ResultState<List<NoticeItem>>): ResultState<ArrayList<BoardDetailItem>>? {
        val list = ArrayList<BoardDetailItem>()
        return when (resultState) {
            is ResultState.Success -> {
                if (resultState.data?.size!! > 0) {
                    for (notice in resultState.data) {
                        list.add(BoardDetailItem(idx = notice.noticeSeq!!, title = notice.noticeTitle!!, url = notice.linkUrl!!))
                    }
                }
                ResultState.Success(list)
            }
            else -> {
                resultState.errorCode?.let { ResultState.Error(it) }
            }
        }
    }
}
