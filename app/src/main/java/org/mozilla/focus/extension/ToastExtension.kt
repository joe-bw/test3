package org.mozilla.focus.extension

import android.content.Context
import android.widget.Toast
import org.mozilla.focus.utils.ToastMessage

fun Context.showToast(toastMessage: ToastMessage) {
    Toast.makeText(this, toastMessage.message ?: getString(toastMessage.stringResId!!, toastMessage.args), toastMessage.duration).show()
}