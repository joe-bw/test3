/*
 * Create by jhong on 2022. 10. 21.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.main.tutorial

import android.os.Bundle
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.R
import org.mozilla.focus.databinding.ActivityTutorialBinding

class TutorialActivity : BaseActivity<ActivityTutorialBinding>(ActivityTutorialBinding::inflate) {

    override fun initView(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TutorialFragment.newInstance())
                .commitNow()
        }
    }
}