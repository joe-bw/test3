/*
 * Create by jhong on 2022. 7. 18.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.navercorp.nid.NaverIdLoginSDK
import com.sorizava.asrplayer.config.LOGIN_TYPE_RELOGIN
import com.sorizava.asrplayer.extension.config
import com.sorizava.asrplayer.ui.base.BaseActivity
import org.mozilla.focus.R
import org.mozilla.focus.databinding.ActivityLogin2Binding

class LoginActivity : BaseActivity<ActivityLogin2Binding>(ActivityLogin2Binding::inflate) {

    private lateinit var viewModel: LoginViewModel

    override fun initView(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this, LoginViewModelFactory(this.application))
            .get(LoginViewModel::class.java)

        val bundle: Bundle? = null

        if (intent.extras != null) {
            val isReLogin = intent.extras!!.getBoolean(LOGIN_TYPE_RELOGIN, false)
            if (isReLogin) {
                bundle?.putBoolean(LOGIN_TYPE_RELOGIN, isReLogin)
            }
        }

        NaverIdLoginSDK.apply {
            showDevelopersLog(true)
            initialize(application, config.naverClientId, config.naverClientSecret, config.naverClientName)
            isShowMarketLink = true
            isShowBottomTab = true
        }

        val fragment = LoginFragment.newInstance()
        bundle?.apply {
            fragment.arguments = bundle
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // facebook 인증 처리
        if (viewModel.callbackManager != null) {
            viewModel.callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }
}