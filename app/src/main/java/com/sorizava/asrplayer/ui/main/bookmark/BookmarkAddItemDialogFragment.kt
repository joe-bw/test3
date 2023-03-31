/*
 * Create by jhong on 2022. 8. 22.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sorizava.asrplayer.data.item.BookmarkItem
import org.mozilla.focus.R
import org.mozilla.focus.databinding.FragmentDialogAddItemBinding
import org.mozilla.focus.databinding.FragmentListDialogBinding

class BookmarkAddItemDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentListDialogBinding? = null

    private val binding get() = _binding!!

    private lateinit var listener: BookmarkAddItemDialogFragmentListener

    private var boardItemList: ArrayList<BookmarkItem>? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog!!.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.layoutFragmentMainBookmark.setBackgroundResource(R.color.background_color)
        binding.listBookmark.adapter = boardItemList?.let { BookmarkItemAdapter(it) }
    }

    private inner class ViewHolder(
        private val itemBinding: FragmentDialogAddItemBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: BookmarkItem) {
            itemBinding.apply {
                Glide.with(imgLogo.rootView)
                    .load(item.img)
                    .centerInside()
                    .into(imgLogo)
                txtName.text = item.name

                layoutItem.setOnClickListener {
                    dismiss()
                    listener.onAddItem(item)
                }
            }
        }
    }

    private inner class BookmarkItemAdapter(private val list: ArrayList<BookmarkItem>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentDialogAddItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(list[position])
        }

        override fun getItemCount(): Int {
            return boardItemList?.size ?: 0
        }
    }

    companion object {
        fun newInstance(list: ArrayList<BookmarkItem>, listener: BookmarkAddItemDialogFragmentListener): BookmarkAddItemDialogFragment =
            BookmarkAddItemDialogFragment().apply {
                this.listener = listener
                this.boardItemList = list
            }
    }

    interface BookmarkAddItemDialogFragmentListener {
        fun onAddItem(item: BookmarkItem)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}