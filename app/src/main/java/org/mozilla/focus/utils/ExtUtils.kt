package org.mozilla.focus.utils

import android.content.ContentProvider

fun ContentProvider.requireContextExt() = context ?: throw IllegalStateException()