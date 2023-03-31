/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.os.Bundle
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.R
import org.mozilla.focus.databinding.ActivityIntro2Binding

class IntroActivity : BaseActivity<ActivityIntro2Binding>(ActivityIntro2Binding::inflate) {

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, IntroFragment.newInstance())
                .commitNow()
        }
    }
}