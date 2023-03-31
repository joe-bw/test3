/*
 * Create by jhong on 2022. 7. 11.
 * Copyright(c) 2022. Sorizava. All rights reserved.
 */

package com.sorizava.asrplayer.ui.intro

import android.annotation.SuppressLint
import android.app.Application
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sorizava.asrplayer.config.EarzoomLoginManager
import com.sorizava.asrplayer.data.IntroState
import com.sorizava.asrplayer.extension.observe
import com.sorizava.asrplayer.extension.switchStartScreenAnimation
import com.sorizava.asrplayer.ui.base.BaseFragment
import com.sorizava.asrplayer.ui.login.LoginActivity
import org.mozilla.focus.R
import org.mozilla.focus.activity.MainActivity
import org.mozilla.focus.databinding.FragmentIntroBinding

/**
 * 인트로 화면이 나타나는 가운데,
 * 버전 확인
 * Firebase 설정
 *
 */
class IntroFragment : BaseFragment<FragmentIntroBinding>(FragmentIntroBinding::inflate) {

    companion object {
        fun newInstance() = IntroFragment()
    }

    private lateinit var viewModel: IntroViewModel

    override fun initView() {
        val context = activity?.applicationContext

        viewModel = ViewModelProvider(this, IntroViewModelFactory(context as Application))
            .get(IntroViewModel::class.java)

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                EarzoomLoginManager.instance?.putDeviceToken(token)
            })
    }

    override fun initViewModelObserver() {
        observe(viewModel.appVersion, ::handleAppVersion)
        observe(viewModel.introState, ::handleValidSettings)
    }

    override fun setBackPressed() {
        super.setBackPressed()
        if (viewModel.introState.value != IntroState.GOTO_MAIN) {
            activity?.finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleAppVersion(version: String) {
        binding.txtVersion.text = String.format(getString(R.string.setting_app_version_prefix)) + " $version"
    }

    private fun handleValidSettings(isValid: IntroState) {
        when(isValid) {
            IntroState.LOADING -> {
                // TODO -> nothing happen, 로딩 바가 필요할 경우 그린다. 현재는 없음.
            }

            IntroState.NEED_APP_UPDATE -> {
                showAppUpdateDialog()
            }

            IntroState.CHECK_LOGIN -> {
                viewModel.checkLoginInfo()
            }

            IntroState.GOTO_LOGIN -> {
                gotoLoginActivity()
            }

            IntroState.GOTO_MAIN -> {
                appStart()
            }
            else -> {}
        }
    }

    private fun gotoLoginActivity() {
        startActivity(Intent(context, LoginActivity::class.java))
        activity?.finish()
        activity?.switchStartScreenAnimation()
    }

    private fun appStart() {
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
        activity?.switchStartScreenAnimation()
    }

    private fun showAppUpdateDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.txt_app_update))
            .setMessage(getString(R.string.txt_app_update_need))
            .setPositiveButton(getString(R.string.txt_app_update)) { _: DialogInterface?, _: Int ->
                val marketLaunch = Intent(Intent.ACTION_VIEW)
                marketLaunch.data =
                    Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                startActivity(marketLaunch)
                activity?.finish()
            }
            .setNegativeButton(getString(R.string.action_cancel)) { _: DialogInterface?, _: Int -> activity?.finish() }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}

class IntroViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(IntroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            IntroViewModel(application) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}