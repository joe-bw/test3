/*
 * Create by jhong on 2022. 1. 10.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.base

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.viewbinding.ViewBinding
import com.sorizava.asrplayer.extension.hideKeyboard
import org.mozilla.focus.R
import org.mozilla.focus.utils.Settings

/** ## Abstract Activity
 *
 * 1. 보일러 코드를 줄이기 위한 목적
 * 2. 데이터 바인딩
 * 3. 라이프싸이클 관리
 *
 */

typealias Inflates<T> = (LayoutInflater) -> T

abstract class BaseActivity<VB : ViewBinding>(private val inflate: Inflates<VB>) : AppCompatActivity() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val setting = Settings.getInstance(this)

        if (setting.isDarkMode() == getString(R.string.preference_theme_dark_mode)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        _binding = inflate.invoke(layoutInflater)

        setContentView(binding.root)

        initView(savedInstanceState)
        initViewModelObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    abstract fun initView(savedInstanceState: Bundle?)
    open fun initViewModelObserver() {}

    /** 포커스 해제 */
    private fun handleFocus(v: View) {
        v.clearFocus()
        v.isFocusable = false
        v.isFocusableInTouchMode = true
        v.isFocusable = true
    }

    /** 포커스를 가지고 있는 뷰의 다른 영역을 touch 시 처리
     * - 키보드 hide
     * - 포커스 해제
     **/
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev!!.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                focusView.hideKeyboard()
                focusView.clearFocus()
            } else {
                handleFocus(focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 폰트 스케일 고정 처리
     * - value: 1.0f
     */
    override fun attachBaseContext(newBase: Context?) {
        val override = Configuration(newBase?.resources?.configuration)
        override.fontScale = 1.0f
        applyOverrideConfiguration(override)
        super.attachBaseContext(newBase)
    }
}
