/*
 * Create by jhong on 2022. 1. 10.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.base

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/** ## Abstract Fragment
 *
 * 1. 보일러 코드를 줄이기 위한 목적
 * 2. 데이터 바인딩
 * 3. 라이프싸이클 관리
 *
 */


typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    // check keyboard hide and show
    private var activityRootView: ViewGroup? = null
    private var isKeyboardShowing = false
    private var onGlobalLayoutListener: OnGlobalLayoutListener? = null

    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModelObserver()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    fun handleFocus(v: View) {
        v.clearFocus()
        v.isFocusable = false
        v.isFocusableInTouchMode = true
        v.isFocusable = true
    }

    private fun onGlobalLayoutListener(view: EditText): OnGlobalLayoutListener? {
        return OnGlobalLayoutListener {
            val r = Rect()
            activityRootView!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = activityRootView!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight > screenHeight * 0.15) {
                if (!isKeyboardShowing) {
                    isKeyboardShowing = true
                }
            } else {
                if (isKeyboardShowing) {
                    isKeyboardShowing = false
                    view.onEditorAction(EditorInfo.IME_ACTION_DONE)
                }
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    fun setKeyboardListener(viewGroup: ViewGroup, view: EditText) {
        activityRootView = viewGroup
        onGlobalLayoutListener = onGlobalLayoutListener(view)
        activityRootView!!.viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    abstract fun initView()
    open fun initViewModelObserver() {}
    open fun setBackPressed() {}
}
