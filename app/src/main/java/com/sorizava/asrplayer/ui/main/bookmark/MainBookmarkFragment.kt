/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.bookmark

import android.annotation.SuppressLint
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sorizava.asrplayer.data.item.BookmarkItem
import com.sorizava.asrplayer.database.java.BookmarkJava
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.extension.toast
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.main.OnPagerViewHandleListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mozilla.components.browser.state.state.SessionState
import org.mozilla.focus.R
import org.mozilla.focus.databinding.FragmentMainBookmarkBinding
import org.mozilla.focus.ext.requireComponents
import org.mozilla.focus.fragment.UrlInputFragment
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MainBookmarkFragment : BaseFragment<FragmentMainBookmarkBinding>(
    FragmentMainBookmarkBinding::inflate
), BookmarkItemSelectedListener<BookmarkItem> {

    private var viewModel: BookmarkViewModel? = null

    private var bookmarkAdapter: MainBookmarkAdapter? = null

    private var listener: OnPagerViewHandleListener? = null

    companion object {
        fun newInstance(listener: OnPagerViewHandleListener) =
            MainBookmarkFragment().apply {
                this.listener = listener
            }
    }

    var isEditModeOrNot = false

    override fun initView() {

        viewModel = ViewModelProvider(this).get(BookmarkViewModel::class.java)

        setupUI()
    }

    private fun setupUI() {
    }

    fun onClickOutsideRecyclerView(){
        offEditMode()
    }

    fun isEditMode(): Boolean {
        return isEditModeOrNot
    }

    private fun offEditMode() {
        viewModel?.setEditMode(false)
    }

    private fun onEditMode() {
        viewModel?.setEditMode(true)
        listener?.onStopViewPagerSwipe(false)
    }

    private fun requestBeAddedBookmarkList() {
        val list = bookmarkAdapter?.differ?.currentList?.toMutableList()
        viewModel!!.requestBeAddedBookmarkList(list)
    }

    override fun initViewModelObserver() {
        observe(viewModel!!.bookmarks, ::handleList)
        observe(viewModel!!.isEditMode, ::handleEditModeList)

        viewModel!!.getDbBookmarks().observe(viewLifecycleOwner, ::handleBeAddedBookmarkList)
    }

    private fun handleBeAddedBookmarkList(dbBookmarks: MutableList<BookmarkJava>?) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val `is`: InputStream =
                    activity?.applicationContext?.assets?.open("bookmarks.json")!!

                val br = BufferedReader(InputStreamReader(`is`))
                var line: String?
                val sb = StringBuilder()
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                br.close()
                val type = object : TypeToken<List<BookmarkJava?>?>() {}.type
                val entireList = Gson().fromJson<List<BookmarkJava>>(sb.toString(), type)

                val allList = entireList?.filter { entire ->
                    dbBookmarks?.none { db ->
                        db.bookmarkId == entire.bookmarkId
                    } == true
                }

                val list = ArrayList<BookmarkItem>()

                allList?.forEach {
                    val res = context?.resources?.getIdentifier(it.imgName,"drawable", context?.packageName)
                    res?.let { img -> BookmarkItem(img, it.bookmarkId, it.position, it.imgName, it.imgUrl, it.name, it.url) }?.let { item -> list.add(item) }
                }

                showBeAddedDialog(list)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun handleList(mutableList: MutableList<BookmarkJava>?) {

        val list = ArrayList<BookmarkItem>()

        mutableList?.forEach {
            val res = context?.resources?.getIdentifier(it.imgName,"drawable", context?.packageName)
            res?.let { img -> BookmarkItem(img, it.bookmarkId, it.position, it.imgName, it.imgUrl, it.name, it.url) }?.let { item -> list.add(item) }
        }

        list.add(BookmarkItem(R.drawable.ic_btn_plus, -1, list.size, "", null, getString(R.string.bookmark_add), "", true))
        bindListData(list = list)

    }

    private fun handleEditModeList(isEditMode: Boolean) {

        isEditModeOrNot = isEditMode

        showTitleEditMode(isEditMode)

        when(isEditMode) {
            true -> {

                // Edit 모드 상태
                bookmarkAdapter?.let {
                    it.setEditMode(true)
                    listener?.onStopViewPagerSwipe(false)
                }
            }
            false -> {
                // Edit 모드 해제
                listener?.onStopViewPagerSwipe(true)

                bookmarkAdapter?.let {
                    it.setEditMode(false)
                    viewModel?.changeItems(it.differ.currentList.toMutableList() as ArrayList<BookmarkItem>)
                }
            }
        }
    }

    private fun showTitleEditMode(show: Boolean) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(UrlInputFragment.FRAGMENT_TAG)
            ?: return

        (fragment as UrlInputFragment).showEditModeTile(show)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListData(list: ArrayList<BookmarkItem>) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.wobble_item)
        bookmarkAdapter = MainBookmarkAdapter(this, animation)
        bookmarkAdapter?.let { adapter ->
            binding.listBookmark.adapter = adapter
            adapter.differ.submitList(list)
            val callback = ItemTouchHelperCallback(adapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(binding.listBookmark)

            adapter.startDrag(object : MainBookmarkAdapter.OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    touchHelper.startDrag(viewHolder)
                }
            })
        }
    }

    override fun onSelectedItem(item: BookmarkItem, mode: Boolean, position: Int) {
        when(mode) {
            true -> {
                if(item.isAdd) {
                    requestBeAddedBookmarkList()
                } else {
                    showEditDialog(item, position)
                }
            }
            false -> {
                if(item.isAdd) {
                    requestBeAddedBookmarkList()
                } else {
                    openUrl(item.url)
                }
            }
        }
    }

    private fun showBeAddedDialog(list: ArrayList<BookmarkItem>) {
        val dialog: DialogFragment = BookmarkAddItemDialogFragment.newInstance(list, object:
            BookmarkAddItemDialogFragment.BookmarkAddItemDialogFragmentListener{
            override fun onAddItem(item: BookmarkItem) {
                bookmarkAdapter?.onAddItem(item)
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "BookmarkAddItemDialogFragment")
    }

    private fun showEditDialog(item: BookmarkItem, position: Int) {
        val dialog: DialogFragment = EditBookmarkDialog(object :
            EditBookmarkDialog.EditBookmarkDialogListener {
            override fun onDialogDeleteClick(dialog: DialogFragment) {
                dialog.dismiss()
                deleteItem(item)
            }

            override fun onDialogChangeNameClick(dialog: DialogFragment) {
                dialog.dismiss()
                showChangeNameDialog(item, position)
            }

        })
        dialog.show(activity?.supportFragmentManager!!, "EditBookmarkDialog")
    }

    override fun onLongSelectedItem(item: BookmarkItem, position: Int) {
        onEditMode()
        showEditDialog(item, position)
    }

    override fun onAppliedItems() {
        onEditMode()
    }

    private fun showChangeNameDialog(item: BookmarkItem, position: Int) {
        val dialog: DialogFragment = ChangeNameBookmarkDialog(object:
            ChangeNameBookmarkDialog.ChangeNameBookmarkDialogListener {
            override fun onDialogYesClick(dialog: DialogFragment, name: String) {
                dialog.dismiss()
                changeItemName(item, name, position)
            }

            override fun onDialogNoClick(dialog: DialogFragment) {
                dialog.dismiss()
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "EditBookmarkDialog")
    }

    private fun changeItemName(item: BookmarkItem, name: String, position: Int) {
        bookmarkAdapter?.let {
            item.name = name
            it.onItemChanged(item)
        }
    }

    private fun deleteItem(item: BookmarkItem) {
        bookmarkAdapter?.let {
            it.onDeleteItem(item)
        }
    }

    private fun openUrl(url: String) {
        requireComponents.tabsUseCases.addTab(
            url,
            source = SessionState.Source.Internal.UserEntered,
            selectTab = true,
            private = true
        )
    }
}


