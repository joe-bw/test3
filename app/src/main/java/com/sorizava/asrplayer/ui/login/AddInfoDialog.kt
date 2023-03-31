/*
 * Create by jhong on 2022. 7. 7.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */
package com.sorizava.asrplayer.ui.login

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.sorizava.asrplayer.extension.beVisible
import org.mozilla.focus.R
import org.mozilla.focus.databinding.DialogAddInfoBinding


class AddInfoDialog(private val listener: AddInfoDialogListener) : DialogFragment() {

    private var _binding: DialogAddInfoBinding? = null
    val binding get() = _binding!!

    interface AddInfoDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, birth: String, phone: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
        fun onDialogPrivacyClick(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = requireContext().resources.displayMetrics
        dialog!!.window!!.setLayout(displayMetrics.widthPixels, displayMetrics.heightPixels)

        binding.apply {
            chkPrivacy.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chkPrivacy.setTextColor(Color.BLACK)
                    chkPrivacy.setError("", null)
                }
            }

            btnConfirm.setOnClickListener(View.OnClickListener {
                if (!chkPrivacy.isChecked) {
                    chkPrivacy.error = getString(R.string.txt_error_permission)
                    chkPrivacy.setTextColor(Color.RED)
                    chkPrivacy.isFocusable = true
                    return@OnClickListener
                }
                if (isValid(view)) {
                    listener.onDialogPositiveClick(
                        this@AddInfoDialog,
                        getDateTime(view),
                        getPhoneValue(view)
                    )
                } else {
                    Snackbar.make(view, getString(R.string.txt_auth_need), Snackbar.LENGTH_SHORT).show()
                }
            })

            btnPrivacy.setOnClickListener { listener.onDialogPrivacyClick(this@AddInfoDialog) }

            btnBirth.setOnClickListener { v ->
                val pickerDialog = DatePickerDialog(
                    requireActivity(),
                    R.style.MyDatePickerDialogStyle,
                    { _, year, month, dayOfMonth ->
                        editYear.setText("" + year)
                        editMonth.setText("" + (month + 1))
                        editDay.setText("" + dayOfMonth)
                        v.visibility = View.GONE
                        layoutBirth.beVisible()
                    },
                    2000,
                    0,
                    1
                )
                pickerDialog.show()
            }
            btnCancel.setOnClickListener { listener.onDialogNegativeClick(this@AddInfoDialog) }
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

    private fun isValid(view: View): Boolean {

        binding.apply {
            val yearValue = editYear.text.toString().trim { it <= ' ' }
            if (yearValue.isEmpty()) return false

            val monthValue = editMonth.text.toString().trim { it <= ' ' }
            if (monthValue.isEmpty()) return false

            val dayValue = editDay.text.toString().trim { it <= ' ' }
            if (dayValue.isEmpty()) return false

            val phoneValue = editPhone.text.toString().trim { it <= ' ' }
            return phoneValue.isNotEmpty()
        }
    }

    private fun getPhoneValue(view: View): String {
        return binding.editPhone.text.toString().trim { it <= ' ' }
    }

    private fun getDateTime(view: View): String {

        binding.apply {
            val yearValue = editYear.text.toString().trim { it <= ' ' }

            var monthValue = editMonth.text.toString().trim { it <= ' ' }
            if (monthValue.length == 1) {
                monthValue = "0$monthValue"
            }

            var dayValue = editDay.text.toString().trim { it <= ' ' }
            if (dayValue.length == 1) {
                dayValue = "0$dayValue"
            }

            val sb = StringBuilder()
            sb.append(yearValue).append(monthValue).append(dayValue)
            return sb.toString()
        }
    }
}