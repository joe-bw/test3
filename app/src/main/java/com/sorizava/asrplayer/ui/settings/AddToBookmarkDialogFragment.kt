/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.sorizava.asrplayer.ui.settings

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.sorizava.asrplayer.database.java.BookmarkJava
import com.sorizava.asrplayer.database.java.BookmarkRepositoryJava
import com.sorizava.asrplayer.extension.toast
import org.mozilla.focus.R
import org.mozilla.focus.ext.application
import org.mozilla.focus.shortcut.IconGenerator

/**
 * Fragment displaying a dialog where a user can change the title for a homescreen shortcut
 */
class AddToBookmarkDialogFragment : DialogFragment() {

    @Suppress("LongMethod")
    override fun onCreateDialog(bundle: Bundle?): AlertDialog {
        val url = requireArguments().getString(URL)
        val title = requireArguments().getString(TITLE)

        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogStyle)
        builder.setCancelable(true)
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_to_bookmark, null)
        builder.setView(dialogView)

        val iconBitmap = IconGenerator.generateLauncherIconPreOreo(
            requireContext(),
            IconGenerator.getRepresentativeCharacter(url)
        )
        val iconView = dialogView.findViewById<ImageView>(R.id.addtobookmark_icon)
        iconView.setImageBitmap(iconBitmap)

        val editableTitle = dialogView.findViewById<EditText>(R.id.edit_title)
        editableTitle.requestFocus()

        if (!TextUtils.isEmpty(title)) {
            editableTitle.setText(title)
            editableTitle.setSelection(title!!.length)
        }

        setButtons(dialogView, editableTitle, url)

        return builder.create()
    }

    private fun setButtons(
        parentView: View,
        editableTitle: EditText,
        iconUrl: String?
    ) {
        val addToBookmarkDialogCancelButton = parentView.findViewById<Button>(R.id.addtobookmark_dialog_cancel)
        val addToBookmarkDialogConfirmButton = parentView.findViewById<Button>(R.id.addtobookmark_dialog_add)

        addToBookmarkDialogCancelButton.setOnClickListener {
            dismiss()
        }

        addToBookmarkDialogConfirmButton.setOnClickListener {

            val bookmark = BookmarkJava(
                100,
                0,
                "img_log_earzoom_light",
                iconUrl,
                editableTitle.text.toString().trim { it <= ' ' },
                iconUrl
            )
            repository?.insertBookmark(bookmark)

            activity?.toast(R.string.add_bookmark_manually_at_main)

            dismiss()
        }
    }

    @Suppress("DEPRECATION") // https://github.com/mozilla-mobile/focus-android/issues/4958
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    companion object {
        const val FRAGMENT_TAG = "add-to-homescreen-prompt-dialog"
        private const val URL = "url"
        private const val TITLE = "title"
        private var repository: BookmarkRepositoryJava? = null

        fun newInstance(
            url: String,
            title: String
        ): AddToBookmarkDialogFragment {
            val frag = AddToBookmarkDialogFragment()
            val args = Bundle()
            args.putString(URL, url)
            args.putString(TITLE, title)
            frag.arguments = args

            this.repository = BookmarkRepositoryJava(frag.context?.application)
            return frag
        }
    }
}
