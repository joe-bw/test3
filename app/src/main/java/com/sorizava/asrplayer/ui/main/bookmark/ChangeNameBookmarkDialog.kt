/*
 * Create by jhong on 2022. 8. 4.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.main.bookmark

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import org.mozilla.focus.databinding.DialogChangeNameBookmarkBinding


class ChangeNameBookmarkDialog(private val listener: ChangeNameBookmarkDialogListener) : DialogFragment() {

    private var _binding: DialogChangeNameBookmarkBinding? = null
    val binding get() = _binding!!

    interface ChangeNameBookmarkDialogListener {
        fun onDialogYesClick(dialog: DialogFragment, name: String)
        fun onDialogNoClick(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogChangeNameBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = requireContext().resources.displayMetrics
        dialog!!.window!!.setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels)

        binding.apply {

            btnYes.setOnClickListener {
                val name = editName.text.toString()
                if (name.isNotEmpty()) {
                    listener.onDialogYesClick(this@ChangeNameBookmarkDialog, name)
                }
            }

            btnNo.setOnClickListener {
                listener.onDialogNoClick(this@ChangeNameBookmarkDialog)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val lpWindow = WindowManager.LayoutParams()
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        lpWindow.dimAmount = 0.8f
        dialog.window!!.attributes = lpWindow
        val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 0)
        dialog.window!!.setBackgroundDrawable(insetDrawable)
        return dialog
    }
}