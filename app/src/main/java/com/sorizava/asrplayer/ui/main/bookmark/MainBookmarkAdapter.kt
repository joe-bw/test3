/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sorizava.asrplayer.data.item.BookmarkItem
import org.mozilla.focus.databinding.AdapterBookmarkItemBinding
import org.mozilla.focus.shortcut.IconGenerator

class MainBookmarkAdapter(
    private val listener: BookmarkItemSelectedListener<BookmarkItem>,
    private var animation: Animation,
) : RecyclerView.Adapter<MainBookmarkAdapter.BookmarkViewHolder>(),
    ItemTouchHelperCallback.OnItemMoveListener {

    private var isEditMode: Boolean = false

    private lateinit var dragListener: OnStartDragListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {

        val itemBinding =
            AdapterBookmarkItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val boardItem = differ.currentList[position]
        holder.bind(boardItem, listener, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setEditMode(mode: Boolean) {
        this.isEditMode = mode
        notifyDataSetChanged()
    }

    private fun isEditMode(): Boolean {
        return this.isEditMode
    }

    inner class BookmarkViewHolder(
        private val itemBinding: AdapterBookmarkItemBinding,
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: BookmarkItem, listener: BookmarkItemSelectedListener<BookmarkItem>, position: Int) {
            itemBinding.apply {

                if (item.imgUrl != null) {

                    val icon = IconGenerator.generateLauncherIcon(itemBinding.root.context, item.imgUrl)

                    Glide.with(imgLogo.rootView)
                        .load(icon)
                        .centerInside()
                        .circleCrop()
                        .into(imgLogo)
                } else {
                    Glide.with(imgLogo.rootView)
                        .load(item.img)
                        .centerInside()
                        .into(imgLogo)
                }

                txtName.text = item.name

                // 편집 모드 상태 전환
                if (isEditMode()) {
                    layoutItem.startAnimation(animation)
                } else {
                    layoutItem.clearAnimation()
                }

                // 모드 상태에 따른 이벤트 처리
                if (isEditMode()) {
                    /** 마지막 항목은 이동할 수 없도록 처리 */
                    if (!item.isAdd) {
                        layoutItem.setOnLongClickListener {
                            dragListener.onStartDrag(this@BookmarkViewHolder)
                            true
                        }
                    }
                } else {
                    layoutItem.setOnLongClickListener {
                        setEditMode(true)
                        listener.onLongSelectedItem(item, position)
                        true
                    }
                }

                layoutItem.setOnClickListener {
                    listener.onSelectedItem(item, isEditMode(), position)
                }
            }
        }
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun startDrag(listener: OnStartDragListener) {
        this.dragListener = listener
    }

    private val differCallback = object: DiffUtil.ItemCallback<BookmarkItem>() {
        override fun areItemsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    /** 마지막 항목으로 이동할 수 없게 처리 */
    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        if (toPosition == (itemCount - 1)) return
        val list = differ.currentList.toMutableList()

        val fromItem = list[fromPosition]
        fromItem.position = toPosition
        list.removeAt(fromPosition)
        list.add(toPosition, fromItem)

        differ.submitList(list)
    }

    fun onItemChanged(item: BookmarkItem) {
        val list = differ.currentList.toMutableList()
        val targetPosition = item.position
        val fromItem = list[targetPosition]
        fromItem.position = targetPosition
        list.removeAt(targetPosition)
        list.add(targetPosition, fromItem)
        differ.submitList(list)

        notifyDataSetChanged()
    }

    fun onDeleteItem(item: BookmarkItem) {
        val list = differ.currentList.toMutableList()
        list.remove(item)
        differ.submitList(list)
    }

    fun onAddItem(item: BookmarkItem) {
        val list = differ.currentList.toMutableList()
        val targetPosition = list.size - 1
        val lastItem = list[targetPosition]
        item.position = targetPosition
        list.removeAt(targetPosition)
        list.add(item)
        list.add(lastItem)
        differ.submitList(list)

        listener.onAppliedItems()
    }
}
