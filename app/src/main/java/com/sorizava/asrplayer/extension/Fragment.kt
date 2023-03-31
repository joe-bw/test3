/*
 * Create by jhong on 2022. 6. 10.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.extension

import androidx.fragment.app.Fragment
import java.io.Serializable

/**
 * fragment 로 argument 를 전달받을 때의 extension
 *
 * <ul>
 *  <li> private val text: String? by argumentParams(INTENT_EXTRA_KEY_TEXT)
 *  <li> private val number: Int? by argumentParams(INTENT_EXTRA_KEY_NUMBER, -1)
 *  <li> private val isOk: Boolean? by argumentParams(INTENT_EXTRA_KEY_IS_OK, false)
 *  <li> private val data: IntentData? by argumentParams(INTENT_EXTRA_KEY_DATA)
 * </ul>
 */
inline fun<reified T: Any> Fragment.argumentParams(key: String, defaultValue: T) = lazy {
    val result = when(defaultValue) {
        is Boolean -> arguments?.getBoolean(key, defaultValue)
        is Int -> arguments?.getInt(key, defaultValue)
        is CharSequence -> arguments?.getString(key)
        is Serializable -> arguments?.getSerializable(key) ?: defaultValue
        else -> throw IllegalArgumentException("IllegalArgument value type [${defaultValue.javaClass}] / key [$key]")
    } as? T
    return@lazy result ?: defaultValue
}
